function[ train_set,test_set] = partitionData( data,percent)
% if nargin < 3
%     select_col = 1:5;
% end
col=size(data,2);
select_col = 1:col;
   
    
    section = floor(length(data) * percent);
   
    
    train_set = data(1:section,select_col);
%     train_target = data(1:section,col);
    test_set = data(section+1:end,select_col) ;
%     test_target = data(section+1:end,col);
end



