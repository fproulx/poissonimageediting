function main()

close all;
clc;


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Implementation of Poission Image Editing Paper
% Code written by: Raffay Hamid (raffay@cc.gatech.edu)
% www.cc.gatech.edu/~raffay
% This is a simple implementation of the above mentioned paper
% Use it at your own risk :-)
%
% The code is simple to interpret as long as you understand the
% basics of the PDE discussed in the paper and some finite difference
% related stuff ( some details may be slightly modified in this
% implementation, so please be careful in using any of these functions
% in some oter code) 
% The code needs some functions written in Pryamid Toolbox available
% at: ftp://ftp.cis.upenn.edu/pub/eero/matlabPyrTools.tar.gz
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



src_img = imread('src.png');
dst_img = imread('dst.png');

imwrite(uint8(src_img),'source_image.png');
imwrite(uint8(dst_img),'destination_image.png');

figure; imshow(uint8(src_img));
figure; imshow(uint8(dst_img));

% selection (équivalent du masque)
img_width = 115;
img_height = 75;

src_st_x = 8; src_st_y = 14; % coin 8 / 14
dst_st_x = 95; dst_st_y = 95; 
% à 95/95 la chèvre devient verte à cause de la mauvaise sélection, le
% delta est trop grand dans l'image source

extracted_src_img = zeros(img_width,img_height,3);
extracted_dst_img = zeros(img_width,img_height,3);

extracted_src_img = src_img(src_st_y:src_st_y+img_height,src_st_x:src_st_x+img_width,:);
extracted_dst_img = dst_img(dst_st_y:dst_st_y+img_height,dst_st_x:dst_st_x+img_width,:);

[rws,cls] = size(extracted_src_img(:,:,1));

dst_img_temp = dst_img;
dst_img_temp(dst_st_y:dst_st_y+img_height,dst_st_x:dst_st_x+img_width,:) = extracted_src_img;
figure; imshow(uint8(dst_img_temp));
imwrite(uint8(dst_img_temp),'simple_cut_and_paste_result.bmp');

padding_factor = 10;
padded_src_img = pad_image(extracted_src_img,padding_factor);
padded_dst_img = pad_image(extracted_dst_img,padding_factor);

figure; imshow(uint8(padded_dst_img));
figure; imshow(uint8(padded_src_img));

temp_result = zeros(rws,cls,3);
temp_result(:,:,1) = find_result_channel(padded_src_img(:,:,1),padded_dst_img(:,:,1),padding_factor);
temp_result(:,:,2) = find_result_channel(padded_src_img(:,:,2),padded_dst_img(:,:,2),padding_factor);
temp_result(:,:,3) = find_result_channel(padded_src_img(:,:,3),padded_dst_img(:,:,3),padding_factor);
figure; imshow(uint8(temp_result));

dst_img(dst_st_y:dst_st_y+img_height,dst_st_x:dst_st_x+img_width,:) = temp_result;
figure; imshow(uint8(dst_img));
imwrite(uint8(dst_img),'final_result.bmp');