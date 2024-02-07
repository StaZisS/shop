import {UserData} from "@/modules/profile/types.ts";

export const mapDataToFormik = (data: UserData, formik : any) => {
    const formattedDate = data.birthDate.split("T")[0];
    formik.setValues({
        fullName: data.fullName,
        gender: data.gender,
        dob: formattedDate,
        email: data.email,
        password: ""
    });
};