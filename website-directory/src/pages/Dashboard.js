import { StyledTitle, StyledSubTitle, Avatar, StyledButton, ButtonGroup, StyledFormArea, colors} from "../components/Style";

//logo
import Logo from "./../assets/logo.png";

const Dashboard = () => {
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

            }}
            >
                <Avatar image={Logo} />
            </div>
            <StyledFormArea bg={colors.dark2}>
                <StyledTitle size={65}>
                Вы успешно вошли в наше приложение
                </StyledTitle>
                <StyledSubTitle size={27}>
                    Через несколько секунд вас перенаправят в контейнер
                </StyledSubTitle>
                <ButtonGroup>
                    <StyledButton to="#">
                        Logout
                    </StyledButton>
                </ButtonGroup>
            </StyledFormArea>
            
       </div>
    );
}

export default Dashboard;