// auth/reducers/sessionReducer.js

const initialState = {
    isAuthenticated: false,
    user: null,
};

const sessionReducer = (state = initialState, action) => {
    switch (action.type) {
        case 'LOGIN_SUCCESS':
            return {
                ...state,
                isAuthenticated: true,
                user: action.payload, // Сохраните информацию о пользователе
            };
        case 'LOGOUT':
            return initialState; // Сброс состояния при выходе
        default:
            return state;
    }
};

export default sessionReducer;