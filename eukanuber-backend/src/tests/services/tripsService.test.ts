import { expect } from "chai";
import Knex from "knex";
import Sinon, { SinonStubbedInstance } from "sinon";

import db from "../../db/db";
import { TripStatus } from "../../models";
import googleMapsService from "../../services/googleMapsService";
import tripsService from "../../services/tripsService";

describe("tripsService", () => {
  let sandbox: Sinon.SinonSandbox;
  let dbMock: SinonStubbedInstance<Knex>;
  let googleMapsServiceMock: SinonStubbedInstance<any>;
  let result: any;
  let expectedResult: any;

  before(() => {
    sandbox = Sinon.createSandbox();
    dbMock = getDbMock();
    googleMapsServiceMock = sandbox.stub(googleMapsService);
  });

  after(() => sandbox.restore());

  describe("#getTrips", () => {
    before(async () => {
      sandbox.reset();
      const dbOutput = [{ pets: "" }, { pets: "" }, { pets: "" }];
      expectedResult = [{ pets: [""] }, { pets: [""] }, { pets: [""] }];
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
        sandbox.reset();
        tripId = "tripId";
        const dbOutput = { tripId, pets: "" };
        expectedResult = { tripId, pets: [""] };
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
        sandbox.reset();
        tripId = "tripId";
        expectedResult = undefined;
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
      sandbox.reset();
      tripId = "tripId";
      status = TripStatus.CLIENT_ACCEPTED;
      const dbOutput = { tripId, pets: "" };
      expectedResult = { tripId, pets: [""] };
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
        sandbox.reset();
        tripId = "tripId";
        driverId = "driverId";
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
        sandbox.reset();
        tripId = "tripId";
        driverId = "driverId";
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

    before(async () => {
      origin = "origin";
      destination = "destination";
      expectedResult = "result";
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

  describe("#getUserLastTrip", () => {
    describe("when the trip is found", () => {
      before(async () => {
        sandbox.reset();
        expectedResult = { id: 1 };
        dbMock.select.returnsThis();
        dbMock.first.resolves(expectedResult);
        result = await tripsService.getUserLastTrip("userId");
      });

      it("should call the db to get the db to get the trip", () => {
        expect(dbMock.table).calledWith("trips");
        expect(dbMock.where).calledWith("clientId", "userId");
        expect(dbMock.orWhere).calledWith("driverId", "userId");
      });

      it("should return the trip", () => {
        expect(result).to.deep.equal(expectedResult);
      });
    });
  });

  describe("#createTrip", () => {
    before(async () => {
      sandbox.reset();
      const routes = JSON.stringify([{ legs: [{ distance: { value: 5000, text: "500km" }, duration: { text: "20min" } }] }]);
      const pets = ["S", "M", "L"];
      const trip = { pets };

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

  function getDbMock(): SinonStubbedInstance<Knex> {
    const mock = sandbox.stub<Knex>(db);
    mock.table.returnsThis();
    mock.orderBy.returnsThis();
    mock.where.returnsThis();
    mock.whereNotNull.returnsThis();
    mock.andWhere.returnsThis();
    mock.orWhere.returnsThis();
    mock.orWhereNot.returnsThis();
    mock.update.returnsThis();
    mock.insert.returnsThis();
    mock.into.returnsThis();
    mock.returning.returnsThis();
    mock.count.returns([{ count: 1 }] as any);
    (mock as any).andWhereNot = () => mock;
    (mock as any).modify = () => mock;

    return mock;
  }
});
