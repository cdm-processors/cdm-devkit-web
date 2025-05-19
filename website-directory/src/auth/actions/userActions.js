import axios from 'axios';

axios.defaults.withCredentials = true;

export const loginUser = (credentials, navigate, setError) => {
    return async (dispatch) => {
        try {
            const response = await axios.post('http://localhost:8080/login', {
                username: credentials.email,
                password: credentials.password,
            });

            localStorage.setItem('userPassword', credentials.password);

            dispatch({ 
                type: 'LOGIN_SUCCESS', 
                payload: {
                    username: credentials.email,
                    ...response.data
                }
            });
            navigate('/dashboard');
        } catch (error) {
            setError(error.response?.data || 'Invalid email or password');
        }
    };
};

export const signupUser = (credentials, navigate, setError) => {
    return async (dispatch) => {
        try {
            const response = await axios.post('http://localhost:8080/registration', {
                username: credentials.email,
                password: credentials.password,
                passwordConfirm: credentials.passwordConfirm,
            });

            dispatch({ type: 'LOGIN_SUCCESS', payload: response.data });
            navigate('/dashboard');
        } catch (error) {
            setError(error.response?.data || 'Registration failed');
        }
    };
};

export const createContainer = (containerData, navigate, setError) => {
    return async (dispatch) => {
        try {
            const loginFormData = new URLSearchParams();
            loginFormData.append('username', containerData.email);
            loginFormData.append('password', localStorage.getItem('userPassword'));

            await axios.post('http://localhost:8080/login', loginFormData, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });

            const containerFormData = new URLSearchParams();
            containerFormData.append('username', containerData.email);

            const response = await axios.post('http://localhost:8080/create-container', 
                containerFormData,
                {
                    withCredentials: true,
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                }
            );

            console.log('Container creation response:', response.data);

            if (response.data) {
                window.open(response.data, '_blank');
            } else {
                console.error('Invalid response:', response.data);
                setError('Invalid response from server');
            }
        } catch (error) {
            console.error('Container creation error:', error.response?.data || error);
            if (error.response?.status === 302 || error.response?.status === 401) {
                setError('Session expired - please log in again');
                navigate('/login');
            } else {
                setError(error.response?.data?.message || 'Container creation failed');
            }
        }
    };
};