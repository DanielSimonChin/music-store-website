if(document.getElementsByTagName("header")[0].getElementsByTagName("ul").length > 0){
    for(let i = 0; i < document.getElementsByTagName("header")[0].getElementsByTagName("ul").length; i++){
        document.getElementsByTagName("header")[0].getElementsByTagName("ul")[i].remove();
    }
}