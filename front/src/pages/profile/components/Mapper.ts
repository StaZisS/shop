import {UserData} from "@/modules/profile/types.ts";

export const mapDataToFormik = (data: UserData, formik : any) => {
    const formattedDate = data.birthDate.split("T")[0];
    formik.setValues({
        name: data.name,
        gender: data.gender,
        birthDate: formattedDate,
        email: data.email,
        password: ""
    });
};