package com.aaacpl.api.services;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.aaacpl.bo.request.lots.BidRequestBO;
import com.aaacpl.bo.request.lots.CreateLotRequestBO;
import com.aaacpl.bo.request.lots.UpdateLotBO;
import com.aaacpl.bo.response.CreateLotResponseBO;
import com.aaacpl.bo.response.UpdateLotResponseBO;
import com.aaacpl.exceptions.lotServiceException.LotNotFoundException;
import com.aaacpl.requestHandlers.LotsRequestHandler;
import com.aaacpl.rest.request.lots.BidRequest;
import com.aaacpl.rest.request.lots.CreateLotRequest;
import com.aaacpl.rest.request.lots.StatusRequest;
import com.aaacpl.rest.request.lots.UpdateLotRequest;
import com.aaacpl.rest.response.lots.BidHistoryResponse;
import com.aaacpl.rest.response.lots.BidResponse;
import com.aaacpl.rest.response.lots.CreateLotResponse;
import com.aaacpl.rest.response.lots.LotNotFoundResponse;
import com.aaacpl.rest.response.lots.LotStatusResponse;
import com.aaacpl.rest.response.lots.LotsListByAccessResponse;
import com.aaacpl.rest.response.lots.LotsListResponse;
import com.aaacpl.rest.response.lots.UpdateLotResponse;
import com.aaacpl.rest.util.ResponseGenerator;
import com.aaacpl.util.DateUtil;
import com.aaacpl.validation.RequestValidation;

@Path("/lots")
public class LotsService {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLot(CreateLotRequest createLotRequest, @HeaderParam("sessionId") String sessionId) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            CreateLotRequestBO createLotRequestBO = new CreateLotRequestBO(
                    createLotRequest.getAuctionId(), createLotRequest.getName(),
                    createLotRequest.getDescription(),
                    createLotRequest.getStartBid(),
                    createLotRequest.getDifferenceFactor(),
                    createLotRequest.getStartDate(), createLotRequest.getEndDate(),
                    createLotRequest.getCreatedBy(),
                    createLotRequest.getUpdatedBy());

            CreateLotResponse createLotResponse = new CreateLotResponse();
            LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
            CreateLotResponseBO createLotResponseBO = lotsRequestHandler
                    .createLot(createLotRequestBO);
            if (createLotResponseBO.getId() != 0) {
                createLotResponse.setSuccessMessage(String
                        .valueOf(createLotResponseBO.getId()));
                createLotResponse.setFailureMessage("");
            } else {
                createLotResponse.setSuccessMessage("");
                createLotResponse.setFailureMessage("FAILURE");
            }
            return ResponseGenerator.generateResponse(createLotResponse);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateLot(UpdateLotRequest updatecreateLotRequest, @HeaderParam("sessionId") String sessionId) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            UpdateLotBO createLotRequestBO = new UpdateLotBO(
                    updatecreateLotRequest.getId(),
                    updatecreateLotRequest.getStatus(),
                    updatecreateLotRequest.getAuctionId(),
                    updatecreateLotRequest.getName(),
                    updatecreateLotRequest.getDescription(),
                    updatecreateLotRequest.getStartBid(),
                    updatecreateLotRequest.getDifferenceFactor(),
                    DateUtil.getTimeStampFromString(updatecreateLotRequest
                            .getStartDate()),
                    DateUtil.getTimeStampFromString(updatecreateLotRequest
                            .getEndDate()),
                    updatecreateLotRequest.getUpdatedBy());

            UpdateLotResponse createLotResponse = new UpdateLotResponse();
            LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
            UpdateLotResponseBO createLotResponseBO = lotsRequestHandler
                    .updateLot(createLotRequestBO);
            if (createLotResponseBO.getId() != 0) {
                createLotResponse.setSuccessMessage(String
                        .valueOf(createLotResponseBO.getId()));
                createLotResponse.setFailureMessage("");
            } else {
                createLotResponse.setSuccessMessage("");
                createLotResponse.setFailureMessage("FAILURE");
            }
            return ResponseGenerator.generateResponse(createLotResponse);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list/{auctionId}")
    public Response getAllLots(@PathParam("auctionId") int auctionId, @HeaderParam("sessionId") String sessionId) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
            LotsListResponse lotsListResponse = new LotsListResponse();

            lotsListResponse.setLotsResponseList(lotsRequestHandler
                    .getAllLots(auctionId));
            return ResponseGenerator.generateResponse(lotsListResponse);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/lotInfo/{id}")
    public Response getLotInfo(@PathParam("id") int id, @HeaderParam("sessionId") String sessionId) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            Object response = null;
            try {
                LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
                response = lotsRequestHandler.getLotById(id);
            } catch (LotNotFoundException l) {
                LotNotFoundResponse lotNotFoundResponse = new LotNotFoundResponse();
                lotNotFoundResponse.setFailureMessage(l.getMessage());
                return ResponseGenerator.generateResponse(lotNotFoundResponse);
            }
            return ResponseGenerator.generateResponse(response);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }
    }

    @GET
    @Path("/byAccess/auction/{auctionId}/user/{userId}/{isTender}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLotsByUserAccess(@PathParam("userId") int userId,@PathParam("isTender") Integer isTender,
                                        @PathParam("auctionId") int auctionId, @HeaderParam("sessionId") String sessionId) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
            LotsListByAccessResponse lotsListResponse = new LotsListByAccessResponse();

            lotsListResponse.setLotsResponseList(lotsRequestHandler
                    .getLotsByAccess(userId, auctionId, isTender));
            return ResponseGenerator.generateResponse(lotsListResponse);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }
    }

    @POST
    @Path("/bid/{isTender}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertBid(BidRequest bidRequest, @HeaderParam("sessionId") String sessionId, @PathParam("isTender") Integer isTender) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            String[] sessionIdParts = sessionId.split("@");
            BidRequestBO bidRequestBO = new BidRequestBO(bidRequest.getLotId(),
                    bidRequest.getUserId(), bidRequest.getBidAmount(),
                    bidRequest.getIpAddress(), bidRequest.getLocalSystemTime(),
                    bidRequest.getBidType().toUpperCase());

            BidResponse bidResponse = new BidResponse();
            LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
            if (lotsRequestHandler.insertBid(bidRequestBO, isTender == 1, Integer.parseInt(sessionIdParts[1]))) {
                bidResponse.setFailureMessage("");
                bidResponse.setSuccessMessage("Bid was Successful");
            } else {
                bidResponse.setFailureMessage("Bid Request Failed");
                bidResponse.setSuccessMessage("");
            }
            return ResponseGenerator.generateResponse(bidResponse);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }
    }

    @POST
    @Path("/status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus(StatusRequest statusRequest, @HeaderParam("sessionId") String sessionId) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
            String[] sessionIdParts = sessionId.split("@");
            int userId = Integer.parseInt(sessionIdParts[1]);
            LotStatusResponse lotStatusresponse = lotsRequestHandler
                    .getLotStatus(statusRequest, userId);
            return ResponseGenerator.generateResponse(lotStatusresponse);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }
    }


    @GET
    @Path("/bidHistory/{lotId}/{isTender}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBidHistory(@PathParam("lotId") int lotId, @HeaderParam("sessionId") String sessionId,
                                  @PathParam("isTender") Integer isTender) {
        if (sessionId != null && RequestValidation.isRequestValid(sessionId)) {
            LotsRequestHandler lotsRequestHandler = new LotsRequestHandler();
            System.out.println("Bid history request = "+ lotId +" = lot id ");
            List<BidHistoryResponse> bidHistoryList = lotsRequestHandler
                    .getBidHistory(lotId, isTender);
            return ResponseGenerator.generateResponse(bidHistoryList);
        } else {
            return ResponseGenerator.generateResponse(RequestValidation.getUnautheticatedResponse());
        }

    }
}
