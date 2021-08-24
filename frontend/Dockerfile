FROM node:14.17.5-alpine as builder

RUN mkdir /usr/src/app
WORKDIR /usr/src/app
ENV PATH /usr/src/app/node_modules/.bin:$PATH
COPY package*.json /usr/src/app/
RUN npm install --silent
RUN npm install react-scripts@4.0.3 -g --silent

COPY . /usr/src/app
RUN npm run build

FROM nginx:mainline-alpine
# nginx의 기본 설정을 삭제하고 임의의 nginx.conf 적용
RUN rm -rf /etc/nginx/conf.d
COPY nginx.conf /etc/nginx/nginx.conf

# react 빌드 산출물을 nginx의 샘플 앱이 사용하던 폴더로 이동
COPY --from=builder /usr/src/app/build /usr/share/nginx/html

# 3000포트 오픈하고 nginx 실행
EXPOSE 3000
CMD ["nginx", "-g", "daemon off;"]