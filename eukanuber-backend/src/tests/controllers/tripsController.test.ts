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

  beforeEach(() => {
    // Creates a mock on the object (see https://sinonjs.org/releases/latest/mocks/)
    tripsServiceMock = Sinon.mock(tripsService);
  });

  beforeEach(() => {
    // Restores all mocked methods
    tripsServiceMock.restore();
  });

  describe("getAll", () => {
    before(() => {
      // This section should be similar as createTrip, except that
      // you need to mock the tripsService.getTrips instead
    });

    it("should call the trips service with the proper parameters", () => {});
    it("should return all trips", () => {});
  });

  describe("getById", () => {
    before(() => {
      // This section should be similar as createTrip, except that
      // you need to mock the tripsService.getTripById instead
    });

    it("should call the trips service", () => {});
    it("should get a trip", () => {});
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

  describe("updateTrip", () => {
    before(() => {
      // This section should be similar as createTrip, except that
      // you need to mock the tripsService.updateTrip instead
    });

    it("should call the trips service with the proper parameters", () => {});
    it("should return the updated trip", () => {});
  });
});
