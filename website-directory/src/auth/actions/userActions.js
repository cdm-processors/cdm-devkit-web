import axios from 'axios';

export const loginUser  = (credentials, navigate, setFieldError, setSubmitting) => {
    return async (dispatch) => {
        try {
            const response = await axios.post('http://localhost:8080/api/auth/login', {
                username: credentials.email,
                password: credentials.password,
            });

            // Если вход успешен,
            dispatch({ type: 'LOGIN_SUCCESS', payload: response.data });
dispatch({ type: 'LOGIN_SUCCESS', payload: response.data });
            console.log('Response *******:', response);
            navigate('/dashboard');
        } catch (error) {
            if (error.response) {
                setFieldError('email', error.response.data);
            } else {
                setFieldError('email', 'Invalid email or password');
            }
            setSubmitting(false);
        }
    };
};

export const signupUser  = (credentials, navigate, setFieldError, setSubmitting) => {
    return async (dispatch) => {
        try {
            await axios.get('http://localhost:8080/api/auth/registration');

            const response = await axios.post('http://localhost:8080/api/auth/registration', {
                username: credentials.email,
                password: credentials.password,
                passwordConfirm: credentials.passwordConfirm,
            });
            dispatch({ type: 'LOGIN_SUCCESS', payload: response.data });

            console.log('Response -------:', response);
            if (response.status === 201) {
                navigate('/dashboard');
            }
        } catch (error) {
            if (error.response) {
                setFieldError('email', error.response.data); 
            } else {
                setFieldError('email', 'Registration failed');
            }
            setSubmitting(false);
        }
    };
};
