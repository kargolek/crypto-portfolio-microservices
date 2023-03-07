FROM nginx:1.21.5-alpine

# Remove the default Nginx configuration file
RUN rm /etc/nginx/conf.d/default.conf

# Copy the Angular app files into the container at /usr/share/nginx/html
COPY dist/client-side /usr/share/nginx/html

# Copy the custom Nginx configuration file into the container at /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start Nginx when the container launches
CMD ["nginx", "-g", "daemon off;"]
## Use a base image with pre-installed dependencies
#FROM node:latest
#
## Set the working directory inside the container
#WORKDIR /app
#
## Copy the package.json and package-lock.json files into the container
#COPY package*.json ./
#
## Install the app's dependencies
#RUN npm install
#
## Copy the rest of the app's code into the container
#COPY . .
#
## Build the app
#RUN npm run build --prod
#
## Use a lightweight web server to serve the app
#FROM nginx:latest
#COPY /dist/client-side /usr/share/nginx/html
## RUN rm /etc/nginx/conf.d/default.conf
#
## Copy the Angular app files into the container at /usr/share/nginx/html
## COPY . /usr/share/nginx/html
#
## Change the ownership of the files to the nginx user and group
#RUN chown -R nginx:nginx /usr/share/nginx/html
#
## Copy the custom Nginx configuration file into the container at /etc/nginx/conf.d/default.conf
## COPY nginx.conf /etc/nginx/conf.d/default.conf
#EXPOSE 80
#CMD ["nginx", "-g", "daemon off;"]
