import axios from 'axios';

axios.defaults.withCredentials = true;

export const loginUser = (credentials, navigate, setError) => {
    return async (dispatch) => {
        try {
            const formData = new URLSearchParams();
            formData.append('username', credentials.email);
            formData.append('password', credentials.password);

            const response = await axios.post('http://localhost:8080/login', formData, {
                withCredentials: true,
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });

            if (response.status === 200) {
                localStorage.setItem('userPassword', credentials.password);
                
                dispatch({ 
                    type: 'LOGIN_SUCCESS', 
                    payload: {
                        username: credentials.email,
                        ...response.data
                    }
                });
                navigate('/dashboard');
            }
        } catch (error) {
            console.error('Login error:', error.response?.data || error);
            setError(error.response?.data || 'Invalid email or password');
        }
    };
};

export const signupUser = (credentials, navigate, setError) => {
    return async (dispatch) => {
        try {
            const formData = new URLSearchParams();
            formData.append('username', credentials.email);
            formData.append('password', credentials.password);
            formData.append('passwordConfirm', credentials.passwordConfirm);

            const response = await axios.post('http://localhost:8080/registration', formData, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });

            if (response.status === 201) {
                dispatch({ 
                    type: 'REGISTRATION_SUCCESS', 
                    payload: {
                        message: 'Registration successful! Please check your email to verify your account.'
                    }
                });
                navigate('/login');
            }
        } catch (error) {
            if (error.response?.status === 400) {
                const errorMessage = error.response.data || 'User with this email already exists';
                setError(errorMessage);
                navigate('/');
                return;
            }
            
            const errorMessage = error.response?.data || 'Registration failed';
            setError(errorMessage);
            console.error('Registration error:', error.response?.data || error);
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