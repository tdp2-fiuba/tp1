import { expect } from "chai";
import Express from "express";
import { before } from "mocha";
import Sinon from "sinon";
import { mockReq, mockRes } from "sinon-express-mock";
import tripsController from "../../controllers/tripsController";
import { tripsService } from "../../services";
import { TripStatus } from "../../models";

describe("tripsController", () => {
  let request: Express.Request;
  let response: Express.Response;
  let tripsServiceMock: Sinon.SinonMock;

  beforeEach(() => {
    // Creates a mock on the object (see https://sinonjs.org/releases/latest/mocks/)
    tripsServiceMock = Sinon.mock(tripsService);
  });

  beforeEach(() => {
    // Restores all mocked methods
    tripsServiceMock.restore();
  });

  describe("someTest", () => {
    before(() => {
      // This section should be similar as createTrip, except that
      // you need to mock the tripsService.updateTrip instead
    });

    it("should call the trips service with the proper parameters", () => {});
    it("should return the updated trip", () => {});
  });

  describe("updateTrip", () => {
    let tripId: any;
    let status: any;
    let updatedTrip: any;
    let updateTripStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      tripId = { id: "mordor123" };
      status = { status: TripStatus.DRIVER_GOING_ORIGIN };
      updatedTrip = { id: "mordor123", origin: "Rivendell", destination: "Mordor", status: TripStatus.DRIVER_GOING_ORIGIN };

      // Create a stub that will replace the tripService implementation and
      // configure it to return the same value it receives (see https://sinonjs.org/releases/v7.3.1/stubs/)
      updateTripStub = tripsServiceMock.expects("updateTripStatus").returns(Promise.resolve(updatedTrip));

      // Create a spy to only record what will happen with the response (see https://sinonjs.org/releases/v7.3.1/spies/)
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>({ params: tripId, body: status } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      // Execute the controller's action and (a)wait for it to finish
      await tripsController.updateTrip(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(updateTripStub).calledOnceWith(tripId.id, updatedTrip.status);
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
      // This section should be similar as createTrip, except that
      // you need to mock the tripsService.getTripById instead
      tripId = { id: "ankhmp12" }
      trip = { id:"ankhmp12", origin: "ankhmp", destination: "circleSea" }

      getTripByIdStub = tripsServiceMock.expects("getTripById").returns(Promise.resolve(trip));

      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>( {params: tripId} as any);
      response = mockRes<Express.Response>({json : responseJsonSpy} as any);

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

      // This section should be similar as createTrip, except that
      // you need to mock the tripsService.getTrips instead
      getAllTripsStub = tripsServiceMock.expects("getTrips").returns(Promise.resolve(trips));

      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>();
      response = mockRes<Express.Response>({json : responseJsonSpy} as any);

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
