import { expect } from "chai";
import Express from "express";
import { before } from "mocha";
import Sinon from "sinon";
import { mockReq, mockRes } from "sinon-express-mock";
import usersController from "../../controllers/usersController";
import { userService } from "../../services";

describe("usersController", () => {
  let request: Express.Request;
  let response: Express.Response;
  let userServiceMock: Sinon.SinonMock;

  beforeEach(() => {
    // Creates a mock on the object (see https://sinonjs.org/releases/latest/mocks/)
    userServiceMock = Sinon.mock(userService);
  });

  beforeEach(() => {
    // Restores all mocked methods
    userServiceMock.restore();
  });

  describe("updateUser", () => {
    before(() => {
      // This section should be similar as createTrip, except that
      // you need to mock the userService.updateUser instead
    });

    it("should call the user service with the proper parameters", () => {});
    it("should return the updated user", () => {});
  });

  describe("getAll", () => {

    let users: any;
    let getAllTripsStub: Sinon.SinonStub;
    let responseJsonSpy: any;
    before(async () => {
      users = [{
        userType: "Passenger",
        firstName: "Arthur",
        lastName: "Dent"
      }];

      getAllTripsStub = userServiceMock.expects("getUsers").returns(Promise.resolve(users));

      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>();
      response = mockRes<Express.Response>({json : responseJsonSpy} as any);

      await usersController.getUsers(request, response);
    });

    it("should call the trips service with the proper parameters", () => {
      expect(getAllTripsStub).to.have.been.calledOnce;
    });
    it("should return all trips", () => {
      expect(responseJsonSpy).calledOnceWith(users);
    });
    
  });

});
