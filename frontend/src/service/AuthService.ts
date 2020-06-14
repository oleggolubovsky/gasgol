import axios from "@/http/axios";


const authService = {
    user: {
        token: ''
    },
    login: (user: any) => {
        const token = user.token;
        if (token) {
            axios.defaults.headers = {
                ...axios.defaults.headers,
                'authorization': 'temp_token=' + token,
                'temp_token': token,
            };

            authService.user.token = user.token;
            localStorage.setItem('token', user.token);
        }
    },
    loginFromStore: () => {
        const token = localStorage.getItem('token');

        if (token) {
            axios.defaults.headers = {
                ...axios.defaults.headers,
                'authorization': 'temp_token=' + token,
                'temp_token': token,
            };

            authService.user.token = token;
        }
    },
    logout: () => {
        axios.get("/users/logout")
            .finally(() => {
                localStorage.removeItem('token');
            });
    },
}


export default authService;
