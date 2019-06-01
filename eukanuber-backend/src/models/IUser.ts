import UserValidationStatus from './UserValidationStatus';
import UserTypes from './UserTypes';
import UserState from './UserState';

export default interface IUser {
  id: string;
  firstName: string;
  lastName: string;
  rating: number;
  position: string;
  access: UserValidationStatus;
  state: UserState;
  firebaseToken?: string;
  userType: UserTypes;
}
