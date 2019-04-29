import ICreateUserData from "./ICreateUserData";

interface Image {
    fileName: string,
    file: Buffer
}

export default interface ICreateDriverUserData { 
    user: ICreateUserData,
    images: Array<Image>
}