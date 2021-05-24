console.log("this is script file")

const toggleSidebar=()=>{

    if($('.sidebar').is(":visible"))
    {
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }else{
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
};



const search=()=>{
	
	let query = $("#search-input").val();
	
	if(query == "")
	{
		//if text box is blank then dont show search-result
		$(".search-result").hide();
	}else{
		
		console.log(query);
		//sending request to server
	let url=`http://localhost:8282/search/${query}`;
		
		fetch(url).then((response) => {
			return response.json();
		})
		.then((data) => {
				
				
				let text = `<div class='list-group'>`;
				
				data.forEach((c) => {
					text +=`<a href='/user/contact/${c.cId}' class='list-group-item list-group-action'> ${c.name} </a>`
				});
				
				
				text+=`</div>`;
				
				$(".search-result").html(text);
				$(".search-result").show();
			
		});
				
		
			
	}
	
};










