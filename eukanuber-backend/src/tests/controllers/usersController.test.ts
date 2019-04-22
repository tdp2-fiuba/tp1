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

  describe("someTest", () => {
    before(() => {
      // This section should be similar as createUser, except that
      // you need to mock the usersService.updateUser instead
    });

    it("should call the users service with the proper parameters", () => {});
    it("should return the updated user", () => {});
  });

  describe("updateUser", () => {
    let userId: any;
    let updatedUser: any;
    let updateUserStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      userId = { id: "terry123" };
      updatedUser = { userType: "Driver", firstName: "Terry", lastName: "Pratchett" };

      // Create a stub that will replace the userService implementation and
      // configure it to return the same value it receives (see https://sinonjs.org/releases/v7.3.1/stubs/)
      updateUserStub = userServiceMock.expects("updateUser").returns(Promise.resolve(updatedUser));

      // Create a spy to only record what will happen with the response (see https://sinonjs.org/releases/v7.3.1/spies/)
      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>({ params: userId, body: updatedUser } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      // Execute the controller's action and (a)wait for it to finish
      await usersController.updateUser(request, response);
    });

    it("should call the users service with the proper parameters", () => {
      expect(updateUserStub).calledOnceWith(userId.id, updatedUser);
    });
    it("should return the updated user", () => {
      expect(responseJsonSpy).calledOnceWith(updatedUser);
    });
  });

  describe("getById", () => {
    let user: any;
    let userId: any;
    let getUserByIdStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      userId = { id: "tp88" }
      user = { id:"tp88", userType: "Driver", firstName: "Terry", lastName: "Pratchett" };

      getUserByIdStub = userServiceMock.expects("getUserById").returns(Promise.resolve(user));

      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>( {params: userId} as any);
      response = mockRes<Express.Response>({json : responseJsonSpy} as any);

      await usersController.getUserById(request, response);
    });

    it("should call the users service", () => {
      expect(getUserByIdStub).calledOnceWith(userId.id);
    });
    it("should get a user", () => {
      expect(responseJsonSpy).calledOnceWith(user)
    });
  });

  describe("getAll", () => {
    let users: any;
    let getAllUsersStub: Sinon.SinonStub;
    let responseJsonSpy: any;
    before(async () => {
      users = [{
        userType: "Passenger",
        firstName: "Arthur",
        lastName: "Dent"
      }];

      getAllUsersStub = userServiceMock.expects("getUsers").returns(Promise.resolve(users));

      responseJsonSpy = Sinon.spy();

      request = mockReq<Express.Request>();
      response = mockRes<Express.Response>({json : responseJsonSpy} as any);

      await usersController.getUsers(request, response);
    });

    it("should call the users service with the proper parameters", () => {
      expect(getAllUsersStub).to.have.been.calledOnce;
    });
    it("should return all users", () => {
      expect(responseJsonSpy).calledOnceWith(users);
    });
  });

  describe("createUser", () => {
    let user: any;
    let createUserStub: Sinon.SinonStub;
    let responseJsonSpy: any;

    before(async () => {
      user = { id:"tp88", userType: "Driver", firstName: "Terry", lastName: "Pratchett" };

      // Create a stub that will replace the userService implementation and
      // configure it to return the same value it receives (see https://sinonjs.org/releases/v7.3.1/stubs/)
      createUserStub = userServiceMock.expects("createUser").returns(Promise.resolve(user));

      // Create a spy to only record what will happen with the response (see https://sinonjs.org/releases/v7.3.1/spies/)
      responseJsonSpy = Sinon.spy();

      // Configure request and response with the mocks (stub and spy)
      request = mockReq<Express.Request>({ body: user } as any);
      response = mockRes<Express.Response>({ json: responseJsonSpy } as any);

      // Execute the controller's action and (a)wait for it to finish
      await usersController.createUser(request, response);
    });

    it("should call the users service with the proper parameters", () => {
      expect(createUserStub).calledOnceWith(user);
    });
    it("should return the newly created user", () => {
      expect(responseJsonSpy).calledOnceWith(user);
    });
  });

});
