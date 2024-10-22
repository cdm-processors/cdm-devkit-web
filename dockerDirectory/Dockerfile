FROM codercom/code-server:latest

COPY vscode-cdm-extension-0.2.1.vsix vscode-cdm-extension-0.2.1.vsix

COPY cdm_devkit-0.2.1-py3-none-any.whl cdm_devkit-0.2.1-py3-none-any.whl

COPY cdm-devkit-misc-0.2.1.tar.gz cdm-devkit-misc-0.2.1.tar.gz

RUN sudo apt-get update

RUN sudo apt-get install -y python3

RUN sudo apt-get install python3-pip -y

RUN pip install cdm_devkit-0.2.1-py3-none-any.whl --break-system-packages

RUN code-server --install-extension ./vscode-cdm-extension-0.2.1.vsix

RUN rm ./vscode-cdm-extension-0.2.1.vsix

RUN rm ./cdm_devkit-0.2.1-py3-none-any.whl

RUN rm ./cdm-devkit-misc-0.2.1.tar.gz

COPY config.yaml .config/code-server/config.yaml

WORKDIR ./user