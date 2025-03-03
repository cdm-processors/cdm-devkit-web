import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useNavigate
import { StyledTitle, StyledSubTitle, Avatar, StyledButton, ButtonGroup, StyledFormArea, colors } from "../components/Style";
import Logo from "./../assets/logo.png";
import axios from 'axios';
import { useSelector } from 'react-redux'; // Импортируйте useSelector

const Dashboard = () => {
    const [containerUrl, setContainerUrl] = useState("");
    const isAuthenticated = useSelector(state => state.session.isAuthenticated); // Получите состояние аутентификации
    const navigate = useNavigate(); // Initialize useNavigate here

    const fetchContainerUrl = async () => {
        try {
            const loginResponse = await axios.post('http://localhost:8080/login', {
                username: "k@g.nsu.ru",
                password: "12121212",
            }, { withCredentials: true }); // Ensure cookies are sent
    
            console.log('Login Response:', loginResponse.data); // Log the login response
    
            // Now, create the container using the authenticated user's username
            const homeResponse = await axios.get('http://localhost:8080/api/auth/home', { withCredentials: true });
            
            if (homeResponse.status === 200) {
                console.log('Home Response:', homeResponse.data); // Log the response from /home
    
                // Create the container
                const response = await axios.post('http://localhost:8080/api/auth/create-container', {}, {
                    withCredentials: true // Ensure cookies are sent
                });
                console.log('Create Container Response:', response.data); // Log the response from creating the container
                setContainerUrl(response.data); // Set the container URL
            }
        } catch (error) {
            console.error("Error fetching container URL:", error);
        }
    };

    const handleDisabledButtonClick = () => {
        navigate('/login');
    };

    return (
        <div>
            <div style={{
                position: "absolute",
                top: 0,
                left: 0,
                backgroundColor: "transparent",
                width: "100%",
                padding: "15px",
                display: "flex",
                justifyContent: "flex-start"
            }}>
                <Avatar image={Logo} />
            </div>
            <StyledFormArea bg={colors.dark2}>
                <StyledTitle size={65}>
                    Вы успешно вошли в наше приложение
                </StyledTitle>
                <StyledSubTitle size={27}>
                    Вы можете перейти в контейнер
                </StyledSubTitle>
                <ButtonGroup>
                    {isAuthenticated ? ( // Проверяем, аутентифицирован ли пользователь
                        <StyledButton onClick={fetchContainerUrl}>
                            Создать контейнер
                        </StyledButton>
                    ) : (
                        <StyledButton to={"/login"} disabled>
                            Пожалуйста, войдите, чтобы создать контейнер
                        </StyledButton>
                    )}
                    {containerUrl && (
                        <StyledButton to={containerUrl} target="_blank">
                            Перейти в контейнер
                        </StyledButton>
                    )}
                </ButtonGroup>
            </StyledFormArea>
        </div>
    );
}

export default Dashboard;
