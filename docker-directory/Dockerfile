FROM codercom/code-server:4.93.1

RUN sudo apt-get update

RUN sudo apt-get install -y python3 python3-pip

RUN pip install cdm-devkit==0.2.1 --break-system-packages

RUN wget https://github.com/cdm-processors/cdm-devkit/releases/download/0.2.1/vscode-cdm-extension-0.2.1.vsix

RUN code-server --install-extension ./vscode-cdm-extension-0.2.1.vsix

RUN rm ./vscode-cdm-extension-0.2.1.vsix

COPY config.yaml .config/code-server/config.yaml

WORKDIR ./user
