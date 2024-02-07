import {useEffect, useState} from "react";
import {useFormik} from "formik";
import "react-datepicker/dist/react-datepicker.css";
import s from "./ProfileForm.module.scss";
import {toast, ToastContainer} from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import {ClientUpdateDto, UserData} from "@/modules/profile/types.ts";
import {validationSchema} from "@/pages/profile/components/Validation.ts";
import {fetchUserProfile, updateProfile} from "@/modules/profile/thunk.ts";
import {initialValue} from "@/pages/profile/components/InitialValues.ts";
import {mapDataToFormik} from "@/pages/profile/components/Mapper.ts";

const ProfileForm = () => {
    const [selectedDate, setSelectedDate] = useState<string | undefined>(undefined);
    const [userData, setUserData] = useState<UserData | undefined>(undefined);

    useEffect(() => {
        setData();
    }, []);

    const setData = async () => {
        const data = await fetchUserProfile();
        setUserData(data);
        if (data !== undefined) mapDataToFormik(data, formik);
    };

    const formik= useFormik({
        initialValues: initialValue,
        validationSchema: validationSchema,
        onSubmit: async (values) => {
            try {
                const updatedData : ClientUpdateDto = {
                    fullName: values.fullName,
                    birthDate: selectedDate
                        ? new Date(selectedDate!).toISOString()
                        : userData?.birthDate,
                    gender: values.gender,
                    email: values.email,
                };

                updateProfile(updatedData);
                toast.success("Профиль успешно обновлен!");
            } catch (error) {
                console.error("Failed to update profile:", error);
            }
        },
    });

    return (
        <form onSubmit={formik.handleSubmit} className={s.form}>
            <h2>Профиль</h2>
            <div className={s.formWrapper}>
                <div className={s.formItem}>
                    <label htmlFor="fullName">Фио</label>
                    <input
                        id="fullName"
                        name="fullName"
                        type="text"
                        onChange={formik.handleChange}
                        onBlur={formik.handleBlur}
                        value={formik.values.fullName}
                    />
                    {formik.touched.fullName && formik.errors.fullName ? (
                        <div>{formik.errors.fullName}</div>
                    ) : null}
                </div>
                <div className={s.formItem}>
                    <label htmlFor="email">Email:</label>
                    <p>{userData?.email}</p>
                </div>
                <div className={s.formItem}>
                    <label htmlFor="birthDate">Дата рождения:</label>
                    <input
                        id="birthDate"
                        name="birthDate"
                        type="date"
                        value={formik.values.birthDate}
                        onChange={(event) => {
                            const selectedDate = event.target.value;
                            setSelectedDate(selectedDate);
                            formik.setFieldValue("birthDate", selectedDate);
                        }}
                    />
                </div>

                <div className={s.formItem}>
                    <label htmlFor="gender">Пол</label>
                    <p>{userData?.gender === "MALE" ? "Мужской" : "Женский"}</p>
                </div>
                <ToastContainer/>
                <button className={s.editButton} type="submit">Обновить</button>
            </div>
        </form>
    );
};

export default ProfileForm;
