import Express from "express";
import { before } from "mocha";
import { expect } from "chai";
import { mockReq, mockRes } from "sinon-express-mock";
import usersController from "./usersController";

describe("usersController", () => {
  let request: Express.Request;
  let response: Express.Response;

  describe("getUsers", () => {
    describe("when called with no data", () => {
      before(() => {
        request = mockReq<Express.Request>();
        response = mockRes<Express.Response>();
        usersController.getUsers(request, response);
      });

      it("should send an empty array", () => {
        expect(response.send).to.be.calledWith([]);
      });
    });
  });
});
