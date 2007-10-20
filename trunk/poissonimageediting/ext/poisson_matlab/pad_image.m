function [padded_image] = pad_image(src_img,padding_factor)

rws = size(src_img,1);
cls = size(src_img,2);
temp = zeros(rws,padding_factor);

src_img_temp_1 = zeros(rws,cls + (2*padding_factor),3);

src_img_temp_1(:,:,1) = [temp src_img(:,:,1) temp];
src_img_temp_1(:,:,2) = [temp src_img(:,:,2) temp];
src_img_temp_1(:,:,3) = [temp src_img(:,:,3) temp];


src_img_temp_2 = zeros(rws+ (2*padding_factor),cls+ (2*padding_factor),3);

rws = size(src_img_temp_2,1);
cls = size(src_img_temp_2,2);

temp = zeros(padding_factor,cls);

src_img_temp_2(:,:,1) = [temp; src_img_temp_1(:,:,1); temp];
src_img_temp_2(:,:,2) = [temp; src_img_temp_1(:,:,2); temp];
src_img_temp_2(:,:,3) = [temp; src_img_temp_1(:,:,3); temp];

padded_image = src_img_temp_2;