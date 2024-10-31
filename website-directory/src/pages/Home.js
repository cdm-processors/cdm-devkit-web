import { StyledTitle, StyledSubTitle, Avatar, StyledButton, ButtonGroup} from "../components/Style";

//logo
import Logo from "./../assets/logo.png";

const Home = () => {
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
            <StyledTitle size={65}>
            Вы зашли на страницу CdmWebDevkit
            </StyledTitle>
            <StyledSubTitle size={27}>
                Зарегистрируйтесь или войдите в аккаунт
            </StyledSubTitle>
            <ButtonGroup>
                <StyledButton to="/login">
                    Login
                </StyledButton>
                <StyledButton to="/signup">
                    Signup
                </StyledButton>
            </ButtonGroup>
       </div>
    );
}

export default Home;