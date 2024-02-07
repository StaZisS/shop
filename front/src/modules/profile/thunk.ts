import axios from "axios";
import {BASE_URL} from "@/shared/constants/url.ts";
import {ClientUpdateDto, UserData} from "@/modules/profile/types.ts";
import {ACCESS_TOKEN_NAME} from "@/shared/constants/jwt.ts";


export const fetchUserProfile = async (): Promise<UserData | undefined> => {
    try {
        const response = await axios.get(
            `${BASE_URL}profile`,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem(ACCESS_TOKEN_NAME)}`,
                },
            }
        );
        localStorage.setItem("userId", response.data.id);
        return response.data;
    } catch (error) {
        console.error("Failed to fetch user data:", error);
    }
};

export const updateProfile = async (updatedData: ClientUpdateDto) => {
    try {
        await axios.put(
            `${BASE_URL}profile`,
            updatedData,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem(ACCESS_TOKEN_NAME)}`,
                },
            }
        );
    } catch (error) {
        throw new Error("Update profile error");
    }
};