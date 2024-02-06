import axios from "axios";
import {BASE_URL} from "@/shared/constants/url.ts";
import {ClientUpdateDto, UserData} from "@/modules/profile/types.ts";


export const fetchUserProfile = async (): Promise<UserData | undefined> => {
    try {
        const response = await axios.get(
            `${BASE_URL}account/profile`,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
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
            `${BASE_URL}account/profile`,
            updatedData,
            {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem("token")}`,
                },
            }
        );
    } catch (error) {
        throw new Error("Update profile error");
    }
};