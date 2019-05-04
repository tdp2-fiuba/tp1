interface Image {
  fileName: string;
  file: Buffer;
}

export default interface ICreateUserData {
  userType: string;
  firstName: string;
  lastName: string;
  fbAccessToken: string;
  position: string;
  fbId: string;
  images: Array<Image>;
}
