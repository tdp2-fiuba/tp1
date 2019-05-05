import UserStatus from "./UserStatus";
import UserTypes from "./UserTypes";

export default interface IUser {
  id: string;
  firstName: string;
  lastName: string;
  rating: number;
  position: string;
  status: UserStatus;
  userType: UserTypes;
}
