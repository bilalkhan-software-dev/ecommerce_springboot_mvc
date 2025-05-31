console.log("loading Suggestion");
const searchInput = document.getElementById('search-input');
const suggestionList = document.getElementById('suggestion-list');
let suggestions = [];
let selectedIndex = -1;

searchInput.addEventListener('keyup', async (e) => {
    const query = searchInput.value.trim();

    // Ignore arrow keys and Enter in this event
    if (['ArrowUp', 'ArrowDown', 'Enter'].includes(e.key)) return;

    if (query.length < 2) {
        suggestionList.classList.add('hidden');
        return;
    }

    try {
        const response = await fetch(`/api/suggestion?search=${encodeURIComponent(query)}`);
        if (!response.ok) throw new Error('Network response was not ok');

        suggestions = await response.json();
        selectedIndex = -1;
        renderSuggestions(suggestions);
    } catch (error) {
        console.error('Error fetching suggestions:', error);
        suggestionList.classList.add('hidden');
    }
});

searchInput.addEventListener('keydown', (e) => {
    const items = suggestionList.querySelectorAll('li');

    if (e.key === 'ArrowDown') {
        e.preventDefault();
        if (suggestions.length === 0) return;
        selectedIndex = (selectedIndex + 1) % suggestions.length;
        updateActiveItem(items);
    } else if (e.key === 'ArrowUp') {
        e.preventDefault();
        if (suggestions.length === 0) return;
        selectedIndex = (selectedIndex - 1 + suggestions.length) % suggestions.length;
        updateActiveItem(items);
    } else if (e.key === 'Enter') {
        e.preventDefault();
        if (selectedIndex >= 0 && suggestions[selectedIndex]) {
            searchInput.value = suggestions[selectedIndex].title;
            suggestionList.classList.add('hidden');
        } else {
            // You can trigger a search here if needed
        }
    }
});

function renderSuggestions(products) {
    suggestionList.innerHTML = '';

    if (products.length === 0) {
        suggestionList.classList.add('hidden');
        return;
    }

    products.forEach((product, index) => {
        const li = document.createElement('li');
        li.textContent = product.title;
        li.className = 'px-4 py-2 hover:bg-gray-100 dark:hover:bg-gray-700 cursor-pointer';
        li.addEventListener('click', () => {
            searchInput.value = product.title;
            suggestionList.classList.add('hidden');
        });
        suggestionList.appendChild(li);
    });

    suggestionList.classList.remove('hidden');
}

function updateActiveItem(items) {
    items.forEach((item, index) => {
        item.classList.remove('bg-gray-200', 'dark:bg-gray-600');
        if (index === selectedIndex) {
            item.classList.add('bg-gray-200', 'dark:bg-gray-600');
            searchInput.value = suggestions[selectedIndex].title;
        }
    });
}
