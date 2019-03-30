"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const controllers_1 = require("./controllers");
const app = express_1.default();
const port = process.env.PORT || 3000;
// Users endpoints
app.get("/users", controllers_1.usersController.getUsers);
// Trips endpoints
// TODO
// Status endpoints
app.get("/ping", controllers_1.statusController.ping);
app.get("/ready", controllers_1.statusController.ready);
app.get("/status", controllers_1.statusController.status);
app.listen(port, () => {
    console.log(`Example app listening on port ${port}!`);
});
//# sourceMappingURL=app.js.map