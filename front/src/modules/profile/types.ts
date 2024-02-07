export interface UserData {
    name: string;
    birthDate: string;
    gender: string;
    address: string;
    email: string;
    id: string;
}

export interface ClientUpdateDto {
    name: string;
    birthDate: string | undefined;
    gender: string;
    email: string;
}