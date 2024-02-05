interface InitialValues {
    fullName: string,
    gender: string,
    password: string,
    email: string,
    dob: Date | null,
}

export const initialValue : InitialValues = {
    fullName: "",
    gender: "MALE",
    password: "",
    email: "",
    dob: null
}
