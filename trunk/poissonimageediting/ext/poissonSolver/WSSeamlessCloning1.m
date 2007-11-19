

im = double(imread('dst.png'));
figure;
imshow(mat2gray(im));

iminsert = double(imread('src.png'));
figure;
imshow(mat2gray(iminsert));

imMask = double(imread('mask.png'));
figure;
imshow(mat2gray(imMask));

[imr img imb] = decomposeRGB(im);
[imir imig imib] = decomposeRGB(iminsert);

offset = [95 95];
imr = poissonSolverSeamlessCloning1(imir, imr, imMask, offset);
img = poissonSolverSeamlessCloning1(imig, img, imMask, offset);
imb = poissonSolverSeamlessCloning1(imib, imb, imMask, offset);

imnew = composeRGB(imr, img, imb);

figure(100);
imshow(mat2gray(imnew));
imwrite(mat2gray(imnew), 'output.png');
% poisson1(50, 51, 5);


% im = double(imread('../images/test001BW.tif', 'TIFF'));
% figure;
% imshow(mat2gray(im));