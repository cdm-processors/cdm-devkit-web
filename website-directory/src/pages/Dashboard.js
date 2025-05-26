import { useState, useEffect } from "react";
import {
  StyledTitle,
  StyledSubTitle,
  Avatar,
  StyledButton,
  ButtonGroup,
  StyledFormArea,
  colors,
  ExtraText,
} from "./../components/Style";

// Logo
import Logo from "./../assets/logo.png";

// Auth & Redux
import { connect } from "react-redux";
import { useNavigate } from "react-router-dom";
import { FiMail, FiLock } from "react-icons/fi";
import { ThreeDots } from "react-loader-spinner";
import { createContainer } from "../auth/actions/userActions";

const Dashboard = ({ user, isAuthenticated, createContainer }) => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    console.log('Current user state:', user);
}, [user]);

  useEffect(() => {
    if (!isAuthenticated) {
      navigate("/login");
    }
    setIsLoading(false);
  }, [isAuthenticated, navigate]);

  // const handleLogout = () => {
  //   navigate("/");
  // };

  const handleCreateContainer = () => {
    const containerData = {
        email: user.username,
        password: localStorage.getItem('userPassword')
    };
    
    createContainer(containerData, navigate, (error) => {
        console.error('Container creation failed:', error);
    });
  };

  return (
    <div
      style={{
        display: 'flex',
        height: '100vh',
        width: '100%',
      }}
    >
      {/* Left Side: Content Section */}
      <div
        style={{
          flex: 1,
          backgroundColor: '#fff',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          alignItems: 'center', 
          padding: '50px',
          position: 'relative',
        }}
      >
        {/* Logo in the top-left corner */}
        <div
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            padding: "15px",
            display: "flex",
            justifyContent: "flex-start",
          }}
        >
          <Avatar image={Logo} />
        </div>

        {/* Main Content */}
        {isLoading ? (
          <div
            style={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              height: "100vh",
            }}
          >
            <ThreeDots color={colors.theme} height={49} width={100} />
          </div>
        ) : (
          <StyledFormArea style={{ width: '100%', maxWidth: '400px', backgroundColor: '#fff' }}>
            <StyledTitle size={40} color={colors.primary}>
              CDM Devkit Web
            </StyledTitle>
            <StyledSubTitle size={20} color={colors.dark2} style={{ marginBottom: '30px' }}>
              Welcome to the web application of CDM Devkit
            </StyledSubTitle>
            <StyledSubTitle size={27} color={colors.dark2}>
              Dashboard
            </StyledSubTitle>
            <StyledSubTitle size={20} color={colors.dark2} style={{ marginBottom: '20px' }}>
              Welcome to your dashboard
            </StyledSubTitle>

            {user && (
              <div style={{ marginBottom: '20px' }}>
                <ExtraText color={colors.dark2}>Your Details:</ExtraText>
                <ExtraText color={colors.dark2}>
                  <FiMail /> Email: {user.username}
                </ExtraText>
                <ExtraText color={colors.dark2}>
                  <FiLock /> Status: {isAuthenticated ? "Authenticated" : "Not Authenticated"}
                </ExtraText>
              </div>
            )}

            <ButtonGroup>
              <StyledButton
                to="#"
                onClick={handleCreateContainer}
                style={{
                  backgroundColor: '#4CAF50', 
                  color: '#fff',
                  borderRadius: '25px',
                  padding: '10px 30px',
                  marginRight: '10px'
                }}
              >
                Create Container
              </StyledButton>
              {/* <StyledButton
                to="#"
                onClick={handleLogout}
                style={{
                  backgroundColor: '#000',
                  color: '#fff',
                  borderRadius: '25px',
                  padding: '10px 30px',
                }}
              >
                Logout
              </StyledButton> */}
            </ButtonGroup>
          </StyledFormArea>
        )}
      </div>

      {/* Right Side: Background Section */}
      <div
                style={{
                    flex: 1,
                    backgroundImage: `url(${require('./../assets/newback2.jpg')})`,
                    backgroundSize: 'cover', 
                    backgroundRepeat: 'no-repeat',
                    backgroundPosition: 'center',
                }}
            />
    </div>
  );
};

const mapStateToProps = ({ session }) => ({
  user: session.user,
  isAuthenticated: session.isAuthenticated,
});

const mapDispatchToProps = {
  createContainer
};

export default connect(mapStateToProps, mapDispatchToProps)(Dashboard);