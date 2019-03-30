"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
function ping(req, res, next) {
    res.sendStatus(200);
    next();
}
function ready(req, res, next) {
    res.sendStatus(200);
    next();
}
function status(req, res, next) {
    const status = {
        version: "TBD"
    };
    res.json(status);
    next();
}
exports.default = {
    ping,
    ready,
    status
};
//# sourceMappingURL=statusController.js.map