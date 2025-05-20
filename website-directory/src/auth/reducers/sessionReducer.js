const initialState = {
    user: null,
    isAuthenticated: false
};

export const sessionReducer = (state = initialState, action) => {
    switch (action.type) {
        case 'LOGIN_SUCCESS':
            return {
                ...state,
                isAuthenticated: true,
                user: action.payload
            };
        case 'REGISTRATION_SUCCESS':
            return {
                ...state,
                registrationMessage: action.payload.message
            };
        default:
            return state;
    }
};