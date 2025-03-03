import { combineReducers } from "redux";

//session
import  sessionReducer  from "./sessionReducer"; // Импортируйте ваш редьюсер

const rootReducer = combineReducers({
    session: sessionReducer
});

export default rootReducer;