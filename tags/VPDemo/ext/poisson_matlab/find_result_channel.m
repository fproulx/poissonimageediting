function result = find_result_channel(src_img,dst_img,padding_factor)

[rws cls] = size(src_img);

src_img_x = find_x_gradient(src_img,cls);
src_img_y = find_y_gradient(src_img,rws);

dst_img_x = find_x_gradient(dst_img,cls);
dst_img_y = find_y_gradient(dst_img,rws);

new_dst_img_x = dst_img_x;
new_dst_img_y = dst_img_y;

new_dst_img_x(2:rws-1,2:cls-1) = src_img_x(2:rws-1,2:cls-1);
new_dst_img_y(2:rws-1,2:cls-1) = src_img_y(2:rws-1,2:cls-1);

new_dst_img_x_x = find_x_gradient_b_diff(new_dst_img_x,cls);
new_dst_img_y_y = find_y_gradient_b_diff(new_dst_img_y,rws);

divergence_field = new_dst_img_x_x + new_dst_img_y_y;

divergence_field = divergence_field(padding_factor:size(divergence_field,1)-padding_factor-1,padding_factor:size(divergence_field,2)-padding_factor-1);
src_img = src_img(padding_factor+1:size(src_img,1)-padding_factor,padding_factor+1:size(src_img,2)-padding_factor);
dst_img = dst_img(padding_factor+1:size(dst_img,1)-padding_factor,padding_factor+1:size(dst_img,2)-padding_factor);

[rws cls] = size(src_img);
%laplace_sparse_matrix = Create_Laplace_Sparse_Matrix(rws, cls);
laplace_sparse_matrix = sp_laplace(rws, cls);

mask = zeros(rws,cls);
mask(:,1) = 1;
mask(:,cls) = 1;
mask(1,:) = 1;
mask(rws,:) = 1;
mask = logical(mask);
mask = mask(:);

divergence_field = divergence_field(:);
src_img = src_img(:);
dst_img = dst_img(:);

x_dash_dash = dst_img(mask);
A_dash_dash = laplace_sparse_matrix(:,mask);
b_dash_dash = A_dash_dash * x_dash_dash;

b_dash = divergence_field - b_dash_dash;

A_dash = laplace_sparse_matrix(~mask,~mask);
b_dash = b_dash(~mask,:);
x_dash = A_dash\b_dash;

result = zeros(size(divergence_field,1),1);

result(~mask,1) = x_dash;
result(mask,1) = x_dash_dash;
result = reshape(result,rws,cls);