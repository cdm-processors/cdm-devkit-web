import Home from "./pages/Home";
import Login from "./pages/Login";
import Signup from "./pages/Signup";
import Dashboard from "./pages/Dashboard";
import { StyledContainer } from "./components/Style";
import { Routes, Route, Navigate } from 'react-router-dom';

import {
  BrowserRouter as Router,
} from 'react-router-dom';

function App() {
  return (
    <Router>
      <StyledContainer>
        <Routes> {}
          <Route path="/signup" element={<Signup />} /> {}
          <Route path="/login" element={<Login />} /> {}
          <Route path="/dashboard" element={<Dashboard />} /> {}
          <Route path="/" element={<Home />} /> {}
          <Route 
                path="/registrationConfirm" 
                element={<Navigate to="/login" />} 
            />
        </Routes>
      </StyledContainer>
    </Router>
  );
}

export default App;