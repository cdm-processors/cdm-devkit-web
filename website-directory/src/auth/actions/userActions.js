export const loginUser  = (credentials, navigate, setFieldError, setSubmitting) => {
    return (dispatch) => {
        const user = {
            name: "George",
            email: "pyy@gmail.com"
        };
        const status = true; 
        if (status) {
            navigate('/dashboard');
        } else {
            setFieldError('email', 'Invalid email or password');
            setSubmitting(false);
        }
    };
};

export const signupUser = (credentials, navigate, setFieldError, setSubmitting) => {

}

export const logoutUser = (credentials, navigate, setFieldError, setSubmitting) => {
    
}