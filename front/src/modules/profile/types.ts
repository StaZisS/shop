export interface UserData {
    fullName: string;
    birthDate: string;
    gender: string;
    address: string;
    email: string;
    id: string;
}

export interface ClientUpdateDto {
    fullName: string;
    birthDate: string | undefined;
    gender: string;
    email: string;
}