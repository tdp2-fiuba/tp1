import { expect } from "chai";
import Express from "express";
import { before } from "mocha";
import Sinon from "sinon";
import { mockReq, mockRes } from "sinon-express-mock";
import tripsController from "../../controllers/tripsController";
import { tripsService } from "../../services";

describe("tripsController", () => {
  let request: Express.Request;
  let response: Express.Response;
  let tripsServiceMock: Sinon.SinonMock;

  before(() => {
    // Creates a mock on the object (see https://sinonjs.org/releases/latest/mocks/)
    tripsServiceMock = Sinon.mock(tripsService);
  });

  before(() => {
    // Restores all mocked methods
    tripsServiceMock.restore();
  });

  describe("updateTrip", () => {
    let trip: any;
    let updatedTrip: any;
    let updateTripStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      // This section should be similar as createTrip, except that
      // the tripsService.updateTrip is mocked instead
      trip = { id:"ankhmp12", origin: "ankhmp", destination: "Circle Sea" }
      updatedTrip = { id:"ankhmp12", origin: "ankhmp", destination: "Cori Celesti" }
      
      responseJsonSpy = Sinon.spy();

      updateTripStub = tripsServiceMock.expects("updateTrip").returns(Promise.resolve(updatedTrip));

      request = mockReq<Express.Request>({ body: trip } as any);
      response = mockRes<Express.Response>( { json: responseJsonSpy } as any);

      await tripsController.updateTrip(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(updateTripStub).calledOnceWith(trip);
    });
    it("should return the updated trip", () => {
      expect(responseJsonSpy).calledOnceWith(updatedTrip);
    });
  });

  describe("getById", () => {
    let trip: any;
    let tripId: any;
    let getTripByIdStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      tripId = { id: "ankhmp12" }
      trip = { id:"ankhmp12", origin: "ankhmp", destination: "circleSea" }
      getTripByIdStub = tripsServiceMock.expects("getTripById").returns(Promise.resolve(trip));
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>({ params: tripId } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      await tripsController.getById(request, response);
    });

    it("should call the trips service", () => {
      expect(getTripByIdStub).calledOnceWith(tripId.id);
    });
    it("should get a trip", () => {
      expect(responseJsonSpy).calledOnceWith(trip)
    });
  });

  describe("getAll", () => {
    let trips: any;
    let getAllTripsStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      trips = [{ origin: "abc", destination: "bcd" }, { origin: "def", destination: "ghi" }];
      getAllTripsStub = tripsServiceMock.expects("getTrips").returns(Promise.resolve(trips));
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>();
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      await tripsController.getAll(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(getAllTripsStub).to.have.been.calledOnce;
    });
    it("should return all trips", () => {
      expect(responseJsonSpy).calledOnceWith(trips);
    });
  });

  describe("createTrip", () => {
    let trip: any;
    let createTripStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      trip = { origin: "abc", destination: "bcd" };

      // Create a stub that will replace the tripService implementation and
      // configure it to return the same value it receives (see https://sinonjs.org/releases/v7.3.1/stubs/)
      createTripStub = tripsServiceMock.expects("createTrip").returns(Promise.resolve(trip));

      // Create a spy to only record what will happen with the response (see https://sinonjs.org/releases/v7.3.1/spies/)
      responseJsonSpy = Sinon.spy();

      // Configure request and response with the mocks (stub and spy)
      request = mockReq<Express.Request>({ body: trip } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      // Execute the controller's action and (a)wait for it to finish
      await tripsController.createTrip(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(createTripStub).calledOnceWith(trip);
    });
    it("should return the newly created trip", () => {
      expect(responseJsonSpy).calledOnceWith(trip);
    });
  });
});
