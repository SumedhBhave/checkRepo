package com.aaacpl.requestHandlers;

import com.aaacpl.dao.*;
import com.aaacpl.dto.auction.AuctionDTO;
import com.aaacpl.dto.department.DepartmentDTO;
import com.aaacpl.dto.liveBidLog.LiveBidLogDTO;
import com.aaacpl.dto.lotAuditLog.LotAuditLogDTO;
import com.aaacpl.dto.lots.LotDTO;
import com.aaacpl.reports.BidHistoryPDFCreator;
import com.aaacpl.reports.BidSheetPDFCreator;
import com.aaacpl.util.DateUtil;
import com.aaacpl.util.LotWiseBidHistoryPDFCreator;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ReportRequestHandler {
    public File getLotWiseHistoryReport(String absolutePath, String fileName, int auctionId, Boolean isTender) {
        File file = null;
        Document doc = new Document();
        PdfWriter docWriter = null;
        try {
            file = new File(absolutePath, fileName);
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(absolutePath + fileName));
            LotsDAO lotsDAO = new LotsDAO();
            AuctionDAO auctionDAO = new AuctionDAO();
            UserLotMapDAO userLotMapDAO = new UserLotMapDAO();
            LotAuditLogDAO lotAuditLogDAO = new LotAuditLogDAO();
            List<LotDTO> lotDTOList = lotsDAO.getAllLots(auctionId,"A");
            AuctionDTO auctionDTO = auctionDAO.getAuctionById(auctionId);
            Iterator<LotDTO> iterator = lotDTOList.iterator();
            doc.open();
            while (iterator.hasNext()) {
                doc.newPage();
                LotDTO lotDTO = iterator.next();

                Map<Integer, String> userNameIdMap = userLotMapDAO.getUserNameIdMap(lotDTO.getId());
                List<LotAuditLogDTO> lotAuditLogDTOs = lotAuditLogDAO.getAuditLog(lotDTO.getId(), isTender);
                if (lotAuditLogDTOs.size() > 0) {
                    //document header attributes
                    doc.addAuthor("betterThanZero");
                    doc.addCreationDate();
                    doc.addProducer();
                    doc.addCreator("aaacpl.com");
                    doc.addTitle("LotWise Bid History");
                    doc.setPageSize(PageSize.LETTER);
                    doc.add(new LotWiseBidHistoryPDFCreator().createPDF(userNameIdMap, lotAuditLogDTOs, lotDTO, auctionDTO, isTender));
                } else {
                    doc.add(new LotWiseBidHistoryPDFCreator().createPDF(userNameIdMap, lotAuditLogDTOs, lotDTO, auctionDTO, isTender));
                }

            }
        } catch (SQLException s) {
            s.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException d) {
            d.printStackTrace();
        } finally {
            if (doc != null) {
                //close the document
                doc.close();
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }


        return file;
    }

    public File getUnauthorizedPDFResponse(String absolutePath, String fileName) {
        File file = null;
        Document doc = new Document();
        PdfWriter docWriter = null;
        try {
            file = new File(absolutePath, fileName);
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(absolutePath + fileName));
            doc.open();

            doc.newPage();


            doc.addAuthor("betterThanZero");
            doc.addCreationDate();
            doc.addProducer();
            doc.addCreator("aaacpl.com");
            doc.addTitle("LotWise Bid History");
            doc.setPageSize(PageSize.LETTER);
            Paragraph paragraph = new Paragraph("Unauthorized Access");
            doc.add(paragraph);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (doc != null) {
                //close the document
                doc.close();
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }


        return file;
    }

    public File getBidHistoryReport(String absolutePath, String fileName, int auctionId, Boolean isTender) {
        File file = null;
        Document doc = new Document();
        PdfWriter docWriter = null;
        try {
            file = new File(absolutePath, fileName);
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(absolutePath + fileName));
            LotsDAO lotsDAO = new LotsDAO();
            AuctionDAO auctionDAO = new AuctionDAO();
            UserLotMapDAO userLotMapDAO = new UserLotMapDAO();
            LotAuditLogDAO lotAuditLogDAO = new LotAuditLogDAO();
            List<LotDTO> lotDTOList = lotsDAO.getAllLots(auctionId, "A");
            AuctionDTO auctionDTO = auctionDAO.getAuctionById(auctionId);
            Iterator<LotDTO> iterator = lotDTOList.iterator();

            Font bfBold12 = new Font(Font.FontFamily.COURIER, 12, Font.BOLD, new BaseColor(0, 0, 0));

            doc.open();
            Boolean isForFirstTime = Boolean.TRUE;
            while (iterator.hasNext()) {
                LotDTO lotDTO = iterator.next();

                Map<Integer, String> userNameIdMap = userLotMapDAO.getUserNameIdMap(lotDTO.getId());
                List<LotAuditLogDTO> lotAuditLogDTOs = lotAuditLogDAO.getAuditLog(lotDTO.getId(), isTender);
                //document header attributes
                doc.addAuthor("betterThanZero");
                doc.addCreationDate();
                doc.addProducer();
                doc.addCreator("aaacpl.com");
                doc.addTitle("LotWise Bid History");
                doc.setPageSize(PageSize.LETTER);
                if (isForFirstTime) {
                    //create a paragraph
                    Paragraph paragraphHeader = new Paragraph("A. A. Auctioneers & Contractors Pvt. Ltd.");
                    paragraphHeader.setAlignment(Paragraph.ALIGN_LEFT);
                    doc.add(paragraphHeader);
                    doc.add(Chunk.NEWLINE);

                    //Add title
                    Paragraph titlePara = new Paragraph();
                    titlePara.setAlignment(Paragraph.ALIGN_CENTER);
                    Chunk title = new Chunk("BID HISTORY");
                    title.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
                    titlePara.add(title);
                    titlePara.setFont(bfBold12);
                    doc.add(titlePara);
                    String startDate = isTender? DateUtil.getDateStringFromTimeStamp(auctionDTO.getTenderStartDate()):DateUtil.getDateStringFromTimeStamp(auctionDTO.getStartDate());
                    String endDate = isTender? DateUtil.getDateStringFromTimeStamp(auctionDTO.getTenderEndDate()):DateUtil.getDateStringFromTimeStamp(auctionDTO.getEndDate());
                    StringBuilder auctionInfo = new StringBuilder(auctionDTO.getName()).append(" (Date :- From ").append(startDate)
                            .append(" To ").append(endDate).append(")");
                    Paragraph paragraphAuctionInfo = new Paragraph(auctionInfo.toString());
                    paragraphAuctionInfo.setAlignment(Paragraph.ALIGN_CENTER);
                    doc.add(paragraphAuctionInfo);
                    doc.add(Chunk.NEWLINE);
                    isForFirstTime = Boolean.FALSE;
                }


                Paragraph paragraphs = new BidHistoryPDFCreator().createPDF(userNameIdMap, lotAuditLogDTOs, lotDTO, auctionDTO, isTender);
                doc.add(paragraphs);
                doc.add(Chunk.NEWLINE);
            }
        } catch (SQLException s) {
            s.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException d) {
            d.printStackTrace();
        } finally {
            if (doc != null) {
                //close the document
                doc.close();
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }
        return file;
    }

    public File getBidSheetReport(String absolutePath, String fileName, int auctionId, Boolean isTender) {
        File file = null;
        Document doc = new Document(PageSize.A4);
        PdfWriter docWriter = null;
        long highestBidTotal = 0l;
        try {
            file = new File(absolutePath, fileName);
            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(absolutePath + fileName));
            LotsDAO lotsDAO = new LotsDAO();
            AuctionDAO auctionDAO = new AuctionDAO();
            UserLotMapDAO userLotMapDAO = new UserLotMapDAO();
            List<LotDTO> lotDTOList = lotsDAO.getAllLots(auctionId, "A");
            AuctionDTO auctionDTO = auctionDAO.getAuctionById(auctionId);
            DepartmentDAO departmentDAO = new DepartmentDAO();
            DepartmentDTO departmentDTO = departmentDAO.getDepartmentById(auctionDTO.getDeptId());
            Iterator<LotDTO> iterator = lotDTOList.iterator();

            Font bfBold12 = new Font(Font.FontFamily.COURIER, 14, Font.BOLD);
            Font bf12 = new Font(Font.FontFamily.COURIER, 9);
            doc.open();
            Boolean isForFirstTime = Boolean.TRUE;
            int counter = 1;
            Boolean isAuctionForward = auctionDTO.getAuctionTypeId() == 1;
            while (iterator.hasNext()) {
                LotDTO lotDTO = iterator.next();

                Map<Integer, String> userNameIdMap = userLotMapDAO.getUserNameIdMap(lotDTO.getId());
                LiveBidLogDAO liveBidLogDAO = new LiveBidLogDAO();
                LiveBidLogDTO liveBidLogDTO = null;
                if(isTender){
                    int maxAmt = new LotAuditLogDAO().getMaxAuditLogAmt(lotDTO.getId());
                    liveBidLogDTO = new LotAuditLogDAO().getMaxAuditLogForLot(lotDTO.getId(), maxAmt);
                }else{
                    liveBidLogDTO = liveBidLogDAO.getAuditLog(lotDTO.getId());
                }

                //document header attributes
                doc.addAuthor("betterThanZero");
                doc.addCreationDate();
                doc.addProducer();
                doc.addCreator("aaacpl.com");
                doc.addTitle("LotWise Bid History");
                doc.setPageSize(PageSize.LETTER);
                if (isForFirstTime) {
                    //create a paragraph
                    Paragraph paragraphHeader = new Paragraph("A. A. Auctioneers & Contractors Pvt. Ltd.");
                    paragraphHeader.setAlignment(Paragraph.ALIGN_LEFT);
                    doc.add(paragraphHeader);
                    doc.add(Chunk.NEWLINE);

                    //Add title
                    Paragraph titlePara = new Paragraph();
                    titlePara.setAlignment(Paragraph.ALIGN_CENTER);
                    Chunk title = new Chunk("BID-SHEET");
                    title.setUnderline(0.1f, -2f); //0.1 thick, -2 y-location
                    title.setFont(bfBold12);
                    titlePara.add(title);
                    doc.add(titlePara);

                    Paragraph paragraphAuctionHeader1 = new Paragraph("A. A. Auctioneers & Contractors Pvt. Ltd.");
                    paragraphAuctionHeader1.setAlignment(Paragraph.ALIGN_CENTER);
                    paragraphAuctionHeader1.setFont(bf12);
                    doc.add(paragraphAuctionHeader1);

                    Paragraph paragraphAuctionHeader2 = new Paragraph("GOVT. APPROVED AUCTIONEER");
                    paragraphAuctionHeader2.setAlignment(Paragraph.ALIGN_CENTER);
                    paragraphAuctionHeader2.setFont(bf12);
                    doc.add(paragraphAuctionHeader2);

                    Paragraph paragraphAddress = new Paragraph("20, Kohinoor Society, Opp BMC School, Link Road, Sakinaka, Mumbai-400072 Phone:25145853");
                    paragraphAddress.setAlignment(Paragraph.ALIGN_CENTER);
                    paragraphAddress.setFont(bf12);
                    doc.add(paragraphAddress);
                    String startDate = "";
                    StringBuilder auctionInfo = new StringBuilder();
                    String auctionDescription = "";
                    if(isTender){
                        auctionInfo.append("Tender Sale held at (www.aaacpl.com) on ");
                        auctionDescription = " Tender Sale No. ("+auctionDTO.getDescription()+")";
                        startDate = DateUtil.getDateStringFromTimeStamp(auctionDTO.getTenderStartDate());
                    }else{
                        auctionInfo.append("E-auction Sale held at (www.aaacpl.com) on ");
                        auctionDescription = " E-auction Sale No. ("+auctionDTO.getDescription()+")";
                        startDate = DateUtil.getDateStringFromTimeStamp(auctionDTO.getStartDate());
                    }

                   auctionInfo.append(startDate).append(auctionDescription);
                    Paragraph paragraphAuctionInfo = new Paragraph(auctionInfo.toString());
                    paragraphAuctionInfo.setAlignment(Paragraph.ALIGN_CENTER);
                    paragraphAddress.setFont(bf12);
                    doc.add(paragraphAuctionInfo);

                    StringBuilder departmentInfo = new StringBuilder("Under Instructions From (")
                            .append(departmentDTO.getName())
                            .append(")");
                    Paragraph paragraphDeptInfo = new Paragraph(departmentInfo.toString());
                    paragraphDeptInfo.setAlignment(Paragraph.ALIGN_CENTER);
                    paragraphAddress.setFont(bf12);
                    doc.add(paragraphDeptInfo);
                    isForFirstTime = Boolean.FALSE;
                    doc.add(Chunk.NEWLINE);
                }

                if (liveBidLogDTO != null) {
                    highestBidTotal = highestBidTotal + liveBidLogDTO.getMaxValue();
                }
                Paragraph paragraphs = new BidSheetPDFCreator().createPDF(userNameIdMap, liveBidLogDTO, lotDTO, counter, isAuctionForward);
                doc.add(paragraphs);
                counter++;
            }
            String bidAmountPara = isAuctionForward ? "Total of Highest Bid Amount: " : "Total of Lowest Bid Amount: ";
            Paragraph highestBidTotalInfo = new Paragraph(bidAmountPara + highestBidTotal);
            highestBidTotalInfo.setAlignment(Paragraph.ALIGN_LEFT);
            highestBidTotalInfo.setFont(bf12);

            doc.add(highestBidTotalInfo);

            Chunk glue = new Chunk(new VerticalPositionMark());
            doc.add(Chunk.NEWLINE);
            Paragraph p = new Paragraph("Signature of Auctioneer");
            p.add(new Chunk(glue));
            p.add("Signature                 ");
            p.setFont(bf12);
            doc.add(p);

            Paragraph p1 = new Paragraph("For A. A. Auctioneers & Contractors Pvt. Ltd.");
            p1.add(new Chunk(glue));
            p1.add("Supervising Officer        ");
            p1.setFont(bf12);
            doc.add(p1);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);
            doc.add(Chunk.NEWLINE);

            Paragraph p2 = new Paragraph("Auctioneer");
            p2.add(new Chunk(glue));
            p2.add("Designation               ");
            p2.setFont(bf12);
            doc.add(p2);
        } catch (SQLException s) {
            s.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException d) {
            d.printStackTrace();
        } finally {
            if (doc != null) {
                //close the document
                doc.close();
            }
            if (docWriter != null) {
                //close the writer
                docWriter.close();
            }
        }
        return file;
    }
}
