"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const mocha_1 = require("mocha");
const chai_1 = require("chai");
const sinon_express_mock_1 = require("sinon-express-mock");
const usersController_1 = __importDefault(require("./usersController"));
describe("usersController", () => {
    let request;
    let response;
    describe("getUsers", () => {
        describe("when called with no data", () => {
            mocha_1.before(() => {
                request = sinon_express_mock_1.mockReq();
                response = sinon_express_mock_1.mockRes();
                usersController_1.default.getUsers(request, response);
            });
            it("should send an empty array", () => {
                chai_1.expect(response.send).to.be.calledWith([]);
            });
        });
    });
});
//# sourceMappingURL=usersController.test.js.map