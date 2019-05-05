import { expect } from "chai";
import Knex from "knex";
import Sinon, { SinonStubbedInstance } from "sinon";

import db from "../../db/db";
import { TripStatus } from "../../models";
import googleMapsService from "../../services/googleMapsService";
import tripsService from "../../services/tripsService";

describe.only("tripsService", () => {
  let sandbox: Sinon.SinonSandbox;
  let dbMock: SinonStubbedInstance<Knex>;
  let result: any;
  let expectedResult: any;

  before(() => (sandbox = Sinon.sandbox.create()));

  describe("#getTrips", () => {
    before(async () => {
      const dbOutput = [{ pets: "" }, { pets: "" }, { pets: "" }];
      expectedResult = [{ pets: [""] }, { pets: [""] }, { pets: [""] }];
      initDbMock();
      dbMock.select.resolves(dbOutput);
      result = await tripsService.getTrips(TripStatus.PENDING);
    });

    it("should call the db to retrieve the trips", () => {
      expect(dbMock.table).calledWith("trips");
    });

    it("should return a list of trips", () => {
      expect(result).to.deep.equal(expectedResult);
    });
  });

  describe("#getTripById", () => {
    let tripId: string;

    describe("when the trip is found", () => {
      before(async () => {
        tripId = "tripId";
        const dbOutput = { tripId, pets: "" };
        expectedResult = { tripId, pets: [""] };
        initDbMock();
        dbMock.select.returnsThis();
        dbMock.first.resolves(dbOutput);
        result = await tripsService.getTripById(tripId);
      });

      it("should call the db to get the trip", () => {
        expect(dbMock.where).calledWith("id", tripId);
        expect(dbMock.table).calledWith("trips");
      });

      it("should return the trip", () => {
        expect(result).to.deep.equal(expectedResult);
      });
    });

    describe("when the trip is not", () => {
      before(async () => {
        tripId = "tripId";
        expectedResult = undefined;
        initDbMock();
        dbMock.select.returnsThis();
        dbMock.first.resolves(undefined);
        result = await tripsService.getTripById(tripId);
      });

      it("should call the db to get the trip", () => {
        expect(dbMock.where).calledWith("id", tripId);
        expect(dbMock.table).calledWith("trips");
      });

      it("should return undefined", () => {
        expect(result).to.deep.equal(expectedResult);
      });
    });
  });

  describe("#updateTripStatus", () => {
    let tripId: string;
    let status: TripStatus;

    before(async () => {
      tripId = "tripId";
      status = TripStatus.CLIENT_ACCEPTED;
      const dbOutput = { tripId, pets: "" };
      expectedResult = { tripId, pets: [""] };
      initDbMock();
      dbMock.select.returnsThis();
      dbMock.first.resolves(dbOutput);
      result = await tripsService.updateTripStatus(tripId, status);
    });

    it("should call the db to get the trip", () => {
      expect(dbMock.update).calledWith({ status });
      expect(dbMock.where).calledWith("id", tripId);
      expect(dbMock.table).calledWith("trips");
    });

    it("should return the updated trip", () => {
      expect(result).to.deep.equal(expectedResult);
    });
  });

  describe("#assignDriverToTrip", () => {
    let tripId: string;
    let driverId: string;

    describe("when there is no driver assigned to a trip", () => {
      before(async () => {
        tripId = "tripId";
        driverId = "driverId";

        initDbMock();
        dbMock.select.returnsThis();
        dbMock.first.resolves({ tripId, pets: "" });

        result = await tripsService.assignDriverToTrip(tripId, driverId);
      });

      it("should call the db to get the trip", () => {
        expect(dbMock.update).calledWith({ driverId, status: TripStatus.DRIVER_GOING_ORIGIN });
        expect(dbMock.where).calledWith("id", tripId);
        expect(dbMock.table).calledWith("trips");
      });
    });

    describe("when there is no driver assigned to a trip", () => {
      before(async () => {
        tripId = "tripId";
        driverId = "driverId";

        initDbMock();
        dbMock.select.returnsThis();
        dbMock.first.resolves({ tripId, pets: "", driverId: "otherDriver" });

        try {
          await tripsService.assignDriverToTrip(tripId, driverId);
        } catch (err) {
          result = err;
        }
      });

      it("should throw an exception", () => {
        expect(result).to.exist;
        expect(result instanceof Error).to.be.true;
        expect(result.message).to.exist;
      });
    });
  });

  describe("#getRoute", () => {
    let origin: string;
    let destination: string;
    let googleMapsServiceMock: SinonStubbedInstance<any>;

    before(async () => {
      origin = "origin";
      destination = "destination";
      expectedResult = "result";
      googleMapsServiceMock = sandbox.stub(googleMapsService);
      googleMapsServiceMock.getDirections.resolves(JSON.stringify([expectedResult]));
      result = await tripsService.getRoute(origin, destination);
    });

    it("should call google service to get directions", () => {
      expect(googleMapsServiceMock.getDirections).calledWith(origin, destination);
    });

    it("should return the routes found", () => {
      expect(result).to.deep.equal(expectedResult);
    });
  });

  describe("#getTripByUserAndStatus", () => {
    describe("when the trip is found", () => {
      before(async () => {
        expectedResult = { id: 1 };
        initDbMock();
        dbMock.select.returnsThis();
        dbMock.first.resolves(expectedResult);
        result = await tripsService.getTripByUserAndStatus("userId", TripStatus.PENDING);
      });

      it("should call the db to get the db to get the trip", () => {
        expect(dbMock.table).calledWith("trips");
        expect(dbMock.where).calledWith("clientId", "userId");
        expect(dbMock.orWhere).calledWith("driverId", "userId");
        expect(dbMock.andWhere).calledWith("status", TripStatus.PENDING);
      });

      it("should return the trip id", () => {
        expect(result).to.deep.equal(expectedResult.id);
      });
    });

    describe("when the trip is not found", () => {
      before(async () => {
        expectedResult = undefined;
        initDbMock();
        dbMock.select.returnsThis();
        dbMock.first.resolves(expectedResult);
        result = await tripsService.getTripByUserAndStatus("userId", TripStatus.PENDING);
      });

      it("should call the db to get the db to get the trip", () => {
        expect(dbMock.table).calledWith("trips");
        expect(dbMock.where).calledWith("clientId", "userId");
        expect(dbMock.orWhere).calledWith("driverId", "userId");
        expect(dbMock.andWhere).calledWith("status", TripStatus.PENDING);
      });

      it("should return undefined", () => {
        expect(result).to.deep.equal(expectedResult);
      });
    });
  });

  describe("#createTrip", () => {
    let googleMapsServiceMock: SinonStubbedInstance<any>;

    before(async () => {
      const routes = JSON.stringify([{ legs: [{ distance: { value: 5000, text: "500km" }, duration: { text: "20min" } }] }]);
      const pets = ["S", "M", "L"];
      const trip = { pets };

      initDbMock();
      googleMapsServiceMock = sandbox.stub(googleMapsService);
      googleMapsServiceMock.getDirections.resolves(routes);

      result = await tripsService.createTrip(trip as any);
    });

    it("should call the db to retrieve the trips", () => {
      expect(googleMapsService.getGeocode).calledTwice;
      expect(googleMapsService.getDirections).calledOnce;
      expect(dbMock.insert).calledOnce;
      expect(dbMock.into).calledWith("trips");
    });

    it("should return a list of trips", () => {
      expect(result).to.be.ok;
    });
  });

  function initDbMock(): SinonStubbedInstance<Knex> {
    if (dbMock) {
      sandbox.restore();
      return;
    }

    const mock = sandbox.stub<Knex>(db);
    mock.table.returnsThis();
    mock.orderBy.returnsThis();
    mock.where.returnsThis();
    mock.orWhere.returnsThis();
    mock.andWhere.returnsThis();
    mock.update.returnsThis();
    mock.insert.returnsThis();
    mock.into.returnsThis();
    mock.returning.returnsThis();
    (mock as any).modify = () => mock;

    dbMock = mock;
  }
});
