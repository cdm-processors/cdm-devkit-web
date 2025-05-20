import { StyledTextInput, StyledFormArea, StyledFormButton, StyledLabel, StyledSubTitle, colors, ButtonGroup, ExtraText, TextLink, CopyrightText } from './../components/Style';
import Logo from './../assets/logo.png';
import { Avatar } from '../components/Style';
import { Formik, Form } from 'formik';
import { TextInput } from '../components/FormLib';
import * as Yup from 'yup';
import { FiMail, FiLock } from 'react-icons/fi';
import { ThreeDots } from 'react-loader-spinner';
import { connect } from 'react-redux';
import { loginUser } from '../auth/actions/userActions';
import { useNavigate } from 'react-router-dom';

const Login = ({ loginUser, registrationMessage }) => {
    const navigate = useNavigate();
    return (
        <div
            style={{
                display: 'flex',
                height: '100vh',
                width: '100%',
            }}
        >
            {/* Left Side: Form Section */}
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
                        position: 'absolute',
                        top: 0,
                        left: 0,
                        padding: '15px',
                        display: 'flex',
                        justifyContent: 'flex-start',
                    }}
                >
                    <Avatar image={Logo} />
                </div>

                {/* Form Area */}
                <StyledFormArea style={{ width: '100%', maxWidth: '400px' }}>
                    <StyledSubTitle color={colors.theme} size={40}>
                        CDM Devkit Web
                    </StyledSubTitle>
                    <StyledSubTitle color={colors.dark2} size={20} style={{ marginBottom: '30px' }}>
                        Welcome to the web application of CDM Devkit
                    </StyledSubTitle>
                    {registrationMessage && (
                        <div style={{
                            padding: '10px',
                            margin: '10px 0',
                            backgroundColor: '#4CAF50',
                            color: 'white',
                            borderRadius: '4px'
                        }}>
                            {registrationMessage}
                        </div>
                    )}
                    <Formik
                        initialValues={{ email: '', password: '' }}
                        validationSchema={Yup.object({
                            email: Yup.string().email('Invalid email address').required('Required'),
                            password: Yup.string().min(8, 'Password is too short').max(30, 'Password is too long').required('Required'),
                        })}
                        onSubmit={(values, { setSubmitting, setFieldError }) => {
                            loginUser(values, navigate, setFieldError, setSubmitting);
                        }}
                    >
                        {({ isSubmitting }) => (
                            <Form>
                                <TextInput
                                    name="email"
                                    type="text"
                                    label="Email"
                                    placeholder="jaja@g.nsu.ru"
                                    icon={<FiMail />}
                                />
                                <TextInput
                                    name="password"
                                    type="password"
                                    label="Password"
                                    placeholder="**********"
                                    icon={<FiLock />}
                                />
                                <ButtonGroup>
                                    {!isSubmitting && (
                                        <StyledFormButton
                                            type="submit"
                                            style={{
                                                backgroundColor: '#000',
                                                color: '#fff',
                                                borderRadius: '25px',
                                                padding: '10px 30px',
                                            }}
                                        >
                                            Login
                                        </StyledFormButton>
                                    )}
                                    {isSubmitting && (
                                        <ThreeDots
                                            color={colors.theme}
                                            height={49}
                                            width={100}
                                        />
                                    )}
                                </ButtonGroup>
                            </Form>
                        )}
                    </Formik>
                    <ExtraText>
                        New here? <TextLink to="/signup">Sign up</TextLink>
                    </ExtraText>
                </StyledFormArea>
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

const mapStateToProps = (state) => ({
    registrationMessage: state.session.registrationMessage
});

export default connect(mapStateToProps, { loginUser })(Login);