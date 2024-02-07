import {createAsyncThunk} from "@reduxjs/toolkit";
import {RegistrationData} from "@/modules/registration/types.ts";
import axios from "axios";
import {BASE_URL} from "@/shared/constants/url.ts";
import {ACCESS_TOKEN_NAME} from "@/shared/constants/jwt.ts";

export const registerUser = createAsyncThunk(
    "auth/register",
    async (data: RegistrationData) => {
        try {
            const response = await axios.post(
                `${BASE_URL}auth/register`,
                data
            );
            localStorage.setItem("email", data.email);
            localStorage.setItem(ACCESS_TOKEN_NAME, response.data.accessToken);
        } catch (error) {
            throw new Error("Login failed");
        }
    }
);