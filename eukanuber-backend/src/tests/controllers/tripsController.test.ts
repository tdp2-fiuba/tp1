import { expect } from "chai";
import Express from "express";
import Sinon, { SinonStubbedInstance } from "sinon";
import { mockReq, mockRes } from "sinon-express-mock";
import tripsController from "../../controllers/tripsController";
import { TripStatus } from "../../models";
import { tripsService } from "../../services";

describe("tripsController", () => {
  let sandbox: Sinon.SinonSandbox;
  let tripsServiceMock: SinonStubbedInstance<any>;
  let request: Express.Request;
  let response: Express.Response;

  before(() => {
    sandbox = Sinon.createSandbox();
    tripsServiceMock = sandbox.stub(tripsService);
  });

  after(() => sandbox.restore());

  describe("updateTripStatus", () => {
    let tripId: any;
    let status: any;
    let updatedTrip: any;
    let responseJsonSpy: any;

    before(async () => {
      sandbox.reset();
      tripId = { id: "mordor123" };
      status = { status: TripStatus.DRIVER_GOING_ORIGIN };
      updatedTrip = { id: "mordor123", origin: "Rivendell", destination: "Mordor", status: TripStatus.DRIVER_GOING_ORIGIN };
      tripsServiceMock.updateTripStatus.returns(Promise.resolve(updatedTrip));

      // Create a spy to only record what will happen with the response (see https://sinonjs.org/releases/v7.3.1/spies/)
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>({ params: tripId, body: status } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      // Execute the controller's action and (a)wait for it to finish
      await tripsController.updateTrip(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(tripsServiceMock.updateTripStatus).calledOnceWith(tripId.id, updatedTrip.status);
    });
    it("should return the updated trip", () => {
      expect(responseJsonSpy).calledOnceWith(updatedTrip);
    });
  });

  describe("updateTripDriver", () => {
    let tripId: any;
    let driverId: any;
    let updatedTrip: any;
    let responseJsonSpy: any;

    before(async () => {
      sandbox.reset();
      tripId = { id: "mordor123" };
      driverId = "gollum123";
      updatedTrip = { id: tripId, origin: "Rivendell", destination: "Mordor", driverId };
      tripsServiceMock.assignDriverToTrip.returns(Promise.resolve(updatedTrip));
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>({ params: tripId, body: updatedTrip } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      await tripsController.updateTrip(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(tripsServiceMock.assignDriverToTrip).calledOnceWith(tripId.id, driverId);
    });
    it("should return the updated trip", () => {
      expect(responseJsonSpy).calledOnceWith(updatedTrip);
    });
  });

  describe("getById", () => {
    let trip: any;
    let tripId: any;
    let responseJsonSpy: any;

    before(async () => {
      tripId = { id: "ankhmp12" };
      trip = { id: "ankhmp12", origin: "ankhmp", destination: "circleSea" };
      tripsServiceMock.getTripById.returns(Promise.resolve(trip));
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>({ params: tripId } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      await tripsController.getById(request, response);
    });

    it("should call the trips service", () => {
      expect(tripsServiceMock.getTripById).calledOnceWith(tripId.id);
    });
    it("should get a trip", () => {
      expect(responseJsonSpy).calledOnceWith(trip);
    });
  });

  describe("getAll", () => {
    let trips: any;
    let responseJsonSpy: any;

    before(async () => {
      trips = [{ origin: "abc", destination: "bcd" }, { origin: "def", destination: "ghi" }];
      tripsServiceMock.getTrips.returns(Promise.resolve(trips));
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>();
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      await tripsController.getAll(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(tripsServiceMock.getTrips).to.have.been.calledOnce;
    });
    it("should return all trips", () => {
      expect(responseJsonSpy).calledOnceWith(trips);
    });
  });

  describe("createTrip", () => {
    let trip: any;
    let responseJsonSpy: any;

    before(async () => {
      trip = { origin: "abc", destination: "bcd" };
      tripsServiceMock.createTrip.returns(Promise.resolve(trip));
      responseJsonSpy = Sinon.spy();

      // Configure request and response with the mocks (stub and spy)
      request = mockReq<Express.Request>({ body: trip } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      // Execute the controller's action and (a)wait for it to finish
      await tripsController.createTrip(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(tripsServiceMock.createTrip).calledOnceWith(trip);
    });
    it("should return the newly created trip", () => {
      expect(responseJsonSpy).calledOnceWith(trip);
    });
  });
});
