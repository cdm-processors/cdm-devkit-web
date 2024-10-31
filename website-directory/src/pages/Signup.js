import { StyledTextInput, StyledFormArea, StyledFormButton, StyledLabel, StyledSubTitle, colors, ButtonGroup, ExtraText, TextLink, CopyrightText } from '../components/Style';
import Logo from './../assets/logo.png';
import { Avatar } from '../components/Style';

// Formik
import { Formik, Form } from 'formik';
import { TextInput } from '../components/FormLib';
import * as Yup from 'yup';
import { FiMail, FiLock, FiUser  } from 'react-icons/fi';
import { ThreeDots } from 'react-loader-spinner';

// Auth & Redux
import { connect } from 'react-redux';
import { signupUser  } from '../auth/actions/userActions';
import { useNavigate } from 'react-router-dom';

const Signup = ({ signupUser  }) => {
    const navigate = useNavigate();
    
    return (
       <div>
            <StyledFormArea>
                <Avatar image={Logo} />
                <StyledSubTitle color={colors.theme} size={30}>Member Signup</StyledSubTitle>
                <Formik
                    initialValues={{ email: '', password: '', repeatPassword: "", courseOfStudy: "", name: "" }} 
                    validationSchema={Yup.object({
                        email: Yup.string().email("Invalid email address").required("Required"),
                        password: Yup.string().min(8, "Password is too short").max(30, "Password is too long").required("Required"),
                        name: Yup.string().required("Required"),
                        courseOfStudy: Yup.string().required("Required"), // Validate course of study
                        repeatPassword: Yup.string().required("Required").oneOf([Yup.ref("password")], "Password must match")
                    })}
                    onSubmit={(values, { setSubmitting, setFieldError }) => {
                       signupUser (values, navigate, setFieldError, setSubmitting);
                    }}
                >
                    {({ isSubmitting }) => (
                        <Form>
                            <TextInput 
                                name="name"
                                type="text"
                                label="Full Name"
                                placeholder="Stepan Mul"
                                icon={<FiUser  />}
                            />

                            <TextInput 
                                name="email"
                                type="text"
                                label="Email Address"
                                placeholder="jaja@g.nsu.ru"
                                icon={<FiMail />}
                            />

                            <TextInput 
                                name="courseOfStudy" 
                                type="text" 
                                label="Course of Study" 
                                placeholder="23213"
                                icon={<FiUser  />} 
                            />

                            <TextInput 
                                name="password"
                                type="password"
                                label="Password"
                                placeholder="**********"
                                icon={<FiLock />}
                            />

                            <TextInput 
                                name="repeatPassword"
                                type="password"
                                label="Repeat Password"
                                placeholder="**********"
                                icon={<FiLock />}
                            />

                            <ButtonGroup>
                                {!isSubmitting && (
                                    <StyledFormButton type="submit">Sign Up</StyledFormButton>
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
                    Already have an account? <TextLink to="/login">Login</TextLink>
                </ExtraText>
            </StyledFormArea>
       </div>
    );
}

export default connect(null, { signupUser  })(Signup);