const initialState = {
    user: null,
    isAuthenticated: false
};

const sessionReducer = (state = initialState, action) => {
    switch (action.type) {
        case 'LOGIN_SUCCESS':
            return {
                ...state,
                isAuthenticated: true,
                user: action.payload
            };
        default:
            return state;
    }
};

export { sessionReducer };