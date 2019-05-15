import UserValidationStatus from './UserValidationStatus';
import UserTypes from './UserTypes';

export default interface IUser {
  id: string;
  firstName: string;
  lastName: string;
  rating: number;
  position: string;
  access: UserValidationStatus;
  userType: UserTypes;
}
