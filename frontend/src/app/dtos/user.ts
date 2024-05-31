export interface UserSelection {
  id?: number;
  email: string;
}

export interface UserLoginDto {
  email: string;
  password: string;
}

export interface UserRegisterDto {
  email: string;
  password: string;
}
