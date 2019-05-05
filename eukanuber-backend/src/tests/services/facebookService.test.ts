import { expect } from "chai";
import Sinon from "sinon";

import axios, { AxiosStatic } from "axios";
import facebokService from "../../services/facebookService";

describe("facebokService", () => {
  let sandbox: Sinon.SinonSandbox;
  let axiosMock: Sinon.SinonStubbedInstance<AxiosStatic>;
  let result: any;
  let expectedResult: any;

  before(() => {
    sandbox = Sinon.createSandbox();
    axiosMock = sandbox.stub(axios);
  });

  after(() => sandbox.restore());

  describe("#getFacebookFriendCount", () => {
    describe("when the call is valid", () => {
      before(async () => {
        sandbox.reset();
        expectedResult = { friends: 999 };
        axiosMock.get.resolves({ status: 200, data: expectedResult } as any);
        result = await facebokService.getFacebookFriendCount("token");
      });

      it("should call the facebook API to get the data", () => {
        expect(axiosMock.get).calledOnce;
      });

      it("should return the friend count", () => {
        expect(result).to.deep.equal(expectedResult);
      });
    });

    describe("when the response doesn't return a 2xx status code", () => {
      before(async () => {
        sandbox.reset();
        axiosMock.get.resolves({ status: 500, statusText: "error" } as any);
        try {
          await facebokService.getFacebookFriendCount("token");
        } catch (e) {
          result = e;
        }
      });

      it("should throw an error", () => {
        expect(result instanceof Error).to.be.true;
      });
    });

    describe("when the response doesn't have data", () => {
      before(async () => {
        sandbox.reset();
        axiosMock.get.resolves({ status: 200, data: undefined } as any);
        try {
          await facebokService.getFacebookFriendCount("token");
        } catch (e) {
          result = e;
        }
      });

      it("should throw an error", () => {
        expect(result instanceof Error).to.be.true;
      });
    });
  });
});
